package com.inventario.msinventario.service;

import org.springframework.stereotype.Service;

import com.inventario.msinventario.clients.CatalogoClient;
import com.inventario.msinventario.clients.ComprasClient;
import com.inventario.msinventario.dto.compraDTOs.CompraDTOMapper;
import com.inventario.msinventario.dto.compraDTOs.CompraRequestDTO;
import com.inventario.msinventario.dto.compraDTOs.CompraResponseDTO;
import com.inventario.msinventario.dto.loteDTOs.LoteDTO;
import com.inventario.msinventario.dto.movimientoDTOs.MovimientoDTO;
import com.inventario.msinventario.dto.stockDTOs.StockDTO;
import com.inventario.msinventario.dto.ventaDTOs.VentaDTOMapper;
import com.inventario.msinventario.dto.ventaDTOs.VentaRequestDTO;
import com.inventario.msinventario.dto.ventaDTOs.VentaResponseDTO;
import com.inventario.msinventario.exceptions.RecursoNuloException;
import com.inventario.msinventario.model.TipoMovimiento;

import lombok.extern.slf4j.Slf4j;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Slf4j
@Service
@AllArgsConstructor
public class InventarioService {

    private final VentaDTOMapper ventaDTOMapper;
    private final CompraDTOMapper compraDTOMapper;
    private final ComprasClient comprasClient;
    private final CatalogoClient catalogoClient;
    private final StockService stockService;
    private final LoteService loteService;
    private final MovimientoService movimientoService;

    // COMPRAS
    // REGISTRAR COMPRA EN LOTE, REAJUSTAR STOCK Y REGISTRAR MOVIMIENTO
    @Transactional
    public CompraResponseDTO registrarCompra(CompraRequestDTO request) {

        log.info("Iniciando registro de compra. ID Compra: {}", request.getIdOrdenCompra());

        // 1. SE OBTIENE Y VALIDA LA COMPRA A TRAVÉS DE FEIGN
        CompraRequestDTO compra = comprasClient.obtenerCompra(request.getIdOrdenCompra());
        if (compra == null) {
            log.warn("Rechazando registro: No se encontró la compra con ID {}.", request.getIdOrdenCompra());
            throw new RecursoNuloException("No se encontró la compra en el sistema.");
        }

        // 2. SE ACTUALIZA EL BALANCES DEL STOCK GLOBAL (BUSCA, SUMA Y GUARDA)
        StockDTO stockActualizadoDTO = stockService.acumularOInicializarStock(request.getSku(), request.getCantidad());

        // 3. SE CREA UN NUEVO LOTE Y SE PERSISTE CON SUS VALIDACIONES
        LoteDTO nuevoLoteDTO = new LoteDTO();
        nuevoLoteDTO.setCodigoLote(request.getCodigoLote());
        nuevoLoteDTO.setCantidad(request.getCantidad());
        nuevoLoteDTO.setFechaVencimiento(request.getFechaVencimiento());
        nuevoLoteDTO.setIdCompra(request.getIdOrdenCompra());
        nuevoLoteDTO.setSku(stockActualizadoDTO.getSku());

        LoteDTO loteGuardadoDTO = loteService.crearLote(nuevoLoteDTO);

        // 4. SE REGISTRA EL MOVIMIENTO AUTOMÁTICO PARA AUDITORÍA
        MovimientoDTO nuevoMovimientoDTO = new MovimientoDTO();
        nuevoMovimientoDTO.setCodigoLote(request.getCodigoLote());
        nuevoMovimientoDTO.setCantidad(request.getCantidad());
        nuevoMovimientoDTO.setReferenciaId(request.getIdOrdenCompra());
        nuevoMovimientoDTO.setTipo(TipoMovimiento.COMPRA);
        nuevoMovimientoDTO.setSku(stockActualizadoDTO.getSku());

        movimientoService.registrarMovimiento(nuevoMovimientoDTO);
        log.info("Compra ID: {} procesada con éxito.", request.getIdOrdenCompra());

        return compraDTOMapper.toResponse(loteGuardadoDTO);
    }

    // VENTAS
    @Transactional
    public VentaResponseDTO procesarVenta(VentaRequestDTO request) {
        log.info("Iniciando procesamiento de venta. ID Venta: {}", request.getIdVenta());

        // 1. DELEGAMOS EL DESCUENTO Y VALIDACIÓN GLOBAL AL STOCKSERVICE
        StockDTO stockActualizadoDTO = stockService.descontarStockPorVenta(request.getSku(), request.getCantidad());

        // 2. DELEGAMOS EL ALGORITMO FEFO AL LOTESERVICE
        String codigoLoteMovimiento = loteService.descontarLotesPorFEFO(request.getSku(), request.getCantidad());

        // 3. CREAMOS Y REGISTRAMOS EL MOVIMIENTO DE FORMA AUTOMÁTICA USANDO DTOS
        MovimientoDTO nuevoMovimientoDTO = new MovimientoDTO();
        nuevoMovimientoDTO.setCantidad(request.getCantidad());
        nuevoMovimientoDTO.setCodigoLote(codigoLoteMovimiento);
        nuevoMovimientoDTO.setReferenciaId(request.getIdVenta());
        nuevoMovimientoDTO.setTipo(TipoMovimiento.VENTA);
        nuevoMovimientoDTO.setSku(stockActualizadoDTO.getSku());

        movimientoService.registrarMovimiento(nuevoMovimientoDTO);

        // 4. SINCRONIZAMOS LA VISIBILIDAD EN EL CATÁLOGO EXTERNO
        visibilidadCatalogo(stockActualizadoDTO.getSku(), stockActualizadoDTO.getCantidadTotal());

        log.info("Venta ID: {} procesada con éxito.", request.getIdVenta());

        // 5. RESPUESTA PROTEGIDA USANDO EL DTO ACTUALIZADO
        return ventaDTOMapper.toResponse(stockActualizadoDTO, request.getCantidad());
    }

    // SINCRONIZA VISIVILIDAD EN ms-catalogo
    public void visibilidadCatalogo(Long sku, Integer cantidadTotal) {
        log.info("Sincronizando visibilidad en Catálogo para el SKU: {}. Stock disponible: {}", sku, cantidadTotal);
        boolean activo = cantidadTotal > 0;
        catalogoClient.cambiarVisibilidad(sku, activo);
        log.info("Visibilidad del SKU {} sincronizada con éxito en ms-catalogo.", sku);
    }

}
