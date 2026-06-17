package com.inventario.msinventario.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.inventario.msinventario.exceptions.StockInsuficienteException;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private StockService stockService;
    @Mock
    private LoteService loteService;
    @Mock
    private MovimientoService movimientoService;
    @Mock
    private VentaDTOMapper ventaDTOMapper;
    @Mock
    private CompraDTOMapper compraDTOMapper;
    @Mock
    private ComprasClient comprasClient;
    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private InventarioService service;

    // Test para verificar el flujo orquestado completo de una compra exitosa
    @Test
    void givenRequestCompraValido_whenRegistrarCompra_thenReturnCompraResponseDTO() {
        // GIVEN
        CompraRequestDTO request = new CompraRequestDTO(); // se crea objeto peticion
        request.setIdOrdenCompra(10L); // se asigna orden identificadora
        request.setSku(1010L); // se vincula sku de prueba
        request.setCantidad(50); // se ingresan unidades de compra
        request.setCodigoLote("LOTE-X"); // se asigna codigo de lote
        request.setFechaVencimiento(LocalDate.now().plusYears(2)); // se define vencimiento a dos años

        CompraRequestDTO compraSimuladaFeign = new CompraRequestDTO(); // se inicializa simulacion del cliente externo
        StockDTO stockActualizadoDTO = new StockDTO(); // se prepara dto balance bodega
        stockActualizadoDTO.setSku(1010L); // se asocia el mismo sku

        LoteDTO loteGuardadoDTO = new LoteDTO(); // se prepara dto del lote persistido
        CompraResponseDTO responseDTO = new CompraResponseDTO(); // se prepara dto salida

        when(comprasClient.obtenerCompra(request.getIdOrdenCompra())).thenReturn(compraSimuladaFeign); // mock obtiene datos de feign
        when(stockService.acumularOInicializarStock(request.getSku(), request.getCantidad())).thenReturn(stockActualizadoDTO); // mock actualiza existencias globales
        when(loteService.crearLote(any(LoteDTO.class))).thenReturn(loteGuardadoDTO); // mock genera nuevo bloque fisico
        when(compraDTOMapper.toResponse(loteGuardadoDTO)).thenReturn(responseDTO); // mock transforma a response cliente

        // WHEN
        CompraResponseDTO resultado = service.registrarCompra(request); // se ejecuta orquestacion del flujo

        // THEN
        assertNotNull(resultado); // se valida recepcion de respuesta armada
        verify(comprasClient, times(1)).obtenerCompra(request.getIdOrdenCompra()); // se audita invocacion al microservicio externo
        verify(stockService, times(1)).acumularOInicializarStock(request.getSku(), request.getCantidad()); // se audita incremento en bodega
        verify(loteService, times(1)).crearLote(any(LoteDTO.class)); // se audita creacion del lote fisico
        verify(movimientoService, times(1)).registrarMovimiento(any(MovimientoDTO.class)); // se audita registro del historico automatico
    }

    // Test para verificar interrupcion por orden inexistente en microservicio de compras
    @Test
    void givenCompraInexistenteEnFeign_whenRegistrarCompra_thenThrowRecursoNuloException() {
        // GIVEN
        CompraRequestDTO request = new CompraRequestDTO(); // se crea peticion
        request.setIdOrdenCompra(99L); // se asigna id fantasma

        when(comprasClient.obtenerCompra(request.getIdOrdenCompra())).thenReturn(null); // mock simula respuesta vacia de feign

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.registrarCompra(request)); // se valida lanzamiento por respuesta nula
        verify(stockService, never()).acumularOInicializarStock(any(), any()); // se comprueba bloqueo de modificacion de stock
        verify(loteService, never()).crearLote(any()); // se comprueba bloqueo de creacion de lote
    }

    // Test para verificar rollback logico por error al intentar crear lote invalido
    @Test
    void givenLoteInvalido_whenRegistrarCompra_thenThrowRuntimeException() {
        // GIVEN
        CompraRequestDTO request = new CompraRequestDTO(); // se crea peticion
        request.setIdOrdenCompra(10L); // id orden valido
        request.setSku(1010L); // sku valido
        request.setCantidad(50); // cantidad valida

        CompraRequestDTO compraSimuladaFeign = new CompraRequestDTO(); // mock respuesta feign
        StockDTO stockActualizadoDTO = new StockDTO(); // mock respuesta bodega
        stockActualizadoDTO.setSku(1010L); // mismo sku

        when(comprasClient.obtenerCompra(request.getIdOrdenCompra())).thenReturn(compraSimuladaFeign); // mock aprueba orden feign
        when(stockService.acumularOInicializarStock(request.getSku(), request.getCantidad())).thenReturn(stockActualizadoDTO); // mock aprueba actualizacion stock
        when(loteService.crearLote(any(LoteDTO.class))).thenThrow(new RuntimeException("Error: Fecha de vencimiento inválida.")); // mock gatilla quiebre en creacion lote

        // WHEN AND THEN
        assertThrows(RuntimeException.class, () -> service.registrarCompra(request)); // se valida burbujeo del error de lote
        verify(movimientoService, never()).registrarMovimiento(any()); // se corrobora que no se guardo auditoria por fallo previo
    }

    // Test para verificar flujo orquestado completo de una venta con entrega exitosa
    @Test
    void givenRequestVentaValido_whenProcesarVenta_thenReturnVentaResponseDTO() {
        // GIVEN
        VentaRequestDTO request = new VentaRequestDTO(); // se crea peticion de salida
        request.setIdVenta(20L); // se asigna identificador de venta
        request.setSku(2020L); // se asocia sku comercial
        request.setCantidad(10); // unidades demandadas

        StockDTO stockActualizadoDTO = new StockDTO(); // se crea balance bodega post-descuento
        stockActualizadoDTO.setSku(2020L); // sku asociado
        stockActualizadoDTO.setCantidadTotal(85); // saldo remanente simulado

        String codigoLoteSimulado = "LOTE-FEFO-01"; // id del lote afectado por rotacion
        VentaResponseDTO responseDTO = new VentaResponseDTO(); // se inicializa dto salida

        when(stockService.descontarStockPorVenta(request.getSku(), request.getCantidad())).thenReturn(stockActualizadoDTO); // mock realiza rebaja global
        when(loteService.descontarLotesPorFEFO(request.getSku(), request.getCantidad())).thenReturn(codigoLoteSimulado); // mock ejecuta algoritmo de vaciado FEFO
        when(ventaDTOMapper.toResponse(stockActualizadoDTO, request.getCantidad())).thenReturn(responseDTO); // mock ensambla respuesta comercial

        // WHEN
        VentaResponseDTO resultado = service.procesarVenta(request); // se ejecuta procesamiento de transaccion

        // THEN
        assertNotNull(resultado); // se valida respuesta despachada
        verify(stockService, times(1)).descontarStockPorVenta(request.getSku(), request.getCantidad()); // se confirma rebaja global bodega
        verify(loteService, times(1)).descontarLotesPorFEFO(request.getSku(), request.getCantidad()); // se confirma ejecucion de orden de rotacion
        verify(movimientoService, times(1)).registrarMovimiento(any(MovimientoDTO.class)); // se confirma asiento historico de salida
    }

    // Test para verificar cancelacion de venta por inexistencia de stock global
    @Test
    void givenStockGlobalInsuficiente_whenProcesarVenta_thenThrowStockInsuficienteException() {
        // GIVEN
        VentaRequestDTO request = new VentaRequestDTO(); // se arma peticion
        request.setIdVenta(20L); // id operacion
        request.setSku(2020L); // sku comercial
        request.setCantidad(500); // demanda desproporcionada

        when(stockService.descontarStockPorVenta(request.getSku(), request.getCantidad()))
                .thenThrow(new StockInsuficienteException("No se puede realizar la venta, no hay stock suficiente")); // mock rechaza operacion inicial

        // THEN
        assertThrows(StockInsuficienteException.class, () -> service.procesarVenta(request)); // se valida freno por quiebre stock
        verify(loteService, never()).descontarLotesPorFEFO(any(), any()); // se comprueba descarte de ejecucion algoritmo FEFO
        verify(movimientoService, never()).registrarMovimiento(any()); // se comprueba cancelacion de registro auditoria
    }

    // Test para verificar rollback por fallo imprevisto en algoritmo de rotacion FEFO
    @Test
    void givenFalloEnAlgoritmoFEFO_whenProcesarVenta_thenThrowRuntimeException() {
        // GIVEN
        VentaRequestDTO request = new VentaRequestDTO(); // se arma peticion
        request.setIdVenta(20L); // id operacion
        request.setSku(2020L); // sku valido
        request.setCantidad(10); // unidades de demanda

        StockDTO stockActualizadoDTO = new StockDTO(); // mock dto stock
        stockActualizadoDTO.setSku(2020L); // sku valido

        when(stockService.descontarStockPorVenta(request.getSku(), request.getCantidad())).thenReturn(stockActualizadoDTO); // mock aprueba descuento global
        when(loteService.descontarLotesPorFEFO(request.getSku(), request.getCantidad()))
                .thenThrow(new RuntimeException("Error interno: Inconsistencia en fechas de vencimiento de lotes.")); // mock rompe ejecucion FEFO

        // THEN
        assertThrows(RuntimeException.class, () -> service.procesarVenta(request)); // se valida bloqueo transaccional
        verify(movimientoService, never()).registrarMovimiento(any()); // se garantiza que no se registro historico debido al error
    }

    // Test para verificar activacion automatica de visibilidad si hay stock disponible
    @Test
    void givenCantidadTotalMayorACero_whenVisibilidadCatalogo_thenActivarVisibilidadEnCatalogo() {
        // GIVEN
        Long sku = 780001L; // se define sku
        Integer cantidadTotal = 10; // unidades disponibles mayores a cero

        // WHEN
        service.visibilidadCatalogo(sku, cantidadTotal); // se procesa sincronizacion externa

        // THEN
        verify(catalogoClient, times(1)).cambiarVisibilidad(sku, true); // se verifica disparo de flag activo en true
    }

    // Test para verificar desactivacion automatica de visibilidad al vaciar stock
    @Test
    void givenCantidadTotalIgualACero_whenVisibilidadCatalogo_thenDesactivarVisibilidadEnCatalogo() {
        // GIVEN
        Long sku = 780001L; // se define sku
        Integer cantidadTotal = 0; // producto sin unidades disponibles

        // WHEN
        service.visibilidadCatalogo(sku, cantidadTotal); // se procesa sincronizacion externa

        // THEN
        verify(catalogoClient, times(1)).cambiarVisibilidad(sku, false); // se verifica disparo de flag activo en false
    }
}