package com.boleta.gestionboleta.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.boleta.gestionboleta.client.ClienteBeneficioClient;
import com.boleta.gestionboleta.client.InventarioClient;
import com.boleta.gestionboleta.client.dto.ClienteRemotoDTO;
import com.boleta.gestionboleta.client.dto.ProductoRemotoDTO;
import com.boleta.gestionboleta.dto.ActualizarBoletaProductosRequestDTO;
import com.boleta.gestionboleta.dto.BoletaProductoRequestDTO;
import com.boleta.gestionboleta.dto.BoletaResponseDTO;
import com.boleta.gestionboleta.dto.CrearBoletaRequestDTO;
import com.boleta.gestionboleta.excepcions.RecursoNoEncontradoException;
import com.boleta.gestionboleta.excepcions.RecursoNuloException;
import com.boleta.gestionboleta.excepcions.ReglaNegocioException;
import com.boleta.gestionboleta.model.Boleta;
import com.boleta.gestionboleta.model.BoletaDetalle;
import com.boleta.gestionboleta.model.ClienteSnapshot;
import com.boleta.gestionboleta.repository.BoletaRepository;
import com.boleta.gestionboleta.repository.RecetaClienteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoletaService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final BigDecimal IVA_RATE = new BigDecimal("0.19");

    private final BoletaRepository boletaRepository;
    private final RecetaClienteRepository recetaClienteRepository;
    private final ClienteBeneficioClient clienteBeneficioClient;
    private final InventarioClient inventarioClient;

    public BoletaResponseDTO crearBoleta(CrearBoletaRequestDTO crearBoletaRequestDTO) {
        if (crearBoletaRequestDTO == null) {
            log.warn("Se intento crear una boleta con request nulo.");
            throw new RecursoNuloException("La boleta no puede ser nula.");
        }

        log.info("Iniciando creacion de boleta. runCliente={}, cantidadItems={}",
                crearBoletaRequestDTO.getRunCliente(),
                crearBoletaRequestDTO.getProductos() != null ? crearBoletaRequestDTO.getProductos().size() : 0);

        CalculoBoleta resultado = calcularBoleta(
                crearBoletaRequestDTO.getRunCliente(),
                crearBoletaRequestDTO.getProductos());

        Boleta boleta = new Boleta();
        boleta.setFolio(generarSiguienteFolio());
        boleta.setFechaEmision(LocalDateTime.now());
        boleta.setAnulada(false);
        aplicarResultado(boleta, resultado);

        Boleta boletaGuardada = boletaRepository.save(boleta);
        log.info("Boleta creada exitosamente. id={}, folio={}, runCliente={}, montoBruto={}",
                boletaGuardada.getId(),
                boletaGuardada.getFolio(),
                boletaGuardada.getCliente().getRun(),
                boletaGuardada.getMontoBruto());
        return BoletaResponseDTO.from(boletaGuardada);
    }

    public BoletaResponseDTO buscarPorId(Long id) {
        return BoletaResponseDTO.from(obtenerBoletaPorId(id));
    }

    public List<BoletaResponseDTO> listarPorRunCliente(String runCliente) {
        return boletaRepository.findByClienteRunOrderByFechaEmisionDesc(runCliente)
                .stream()
                .map(BoletaResponseDTO::from)
                .toList();
    }

    public List<BoletaResponseDTO> listarPorSkuProducto(Long skuProducto) {
        return boletaRepository.findBySkuProducto(skuProducto)
                .stream()
                .map(BoletaResponseDTO::from)
                .toList();
    }

    public BoletaResponseDTO actualizarProductos(Long id,
            ActualizarBoletaProductosRequestDTO actualizarBoletaProductosRequestDTO) {
        if (actualizarBoletaProductosRequestDTO == null) {
            log.warn("Se intento actualizar productos de boleta con request nulo. id={}", id);
            throw new RecursoNuloException("La actualizacion de productos no puede ser nula.");
        }

        log.info("Actualizando productos de boleta. id={}, cantidadItems={}",
                id,
                actualizarBoletaProductosRequestDTO.getProductos() != null
                        ? actualizarBoletaProductosRequestDTO.getProductos().size()
                        : 0);

        Boleta boleta = obtenerBoletaPorId(id);
        if (boleta.isAnulada()) {
            log.warn("Se intento editar una boleta anulada. id={}", id);
            throw new ReglaNegocioException("No es posible editar una boleta anulada.");
        }

        CalculoBoleta resultado = calcularBoleta(
                boleta.getCliente().getRun(),
                actualizarBoletaProductosRequestDTO.getProductos());

        aplicarResultado(boleta, resultado);
        Boleta boletaActualizada = boletaRepository.save(boleta);
        log.info("Boleta actualizada exitosamente. id={}, folio={}, montoBruto={}",
                boletaActualizada.getId(),
                boletaActualizada.getFolio(),
                boletaActualizada.getMontoBruto());
        return BoletaResponseDTO.from(boletaActualizada);
    }

    public void anularBoleta(Long id) {
        log.info("Solicitando anulacion de boleta. id={}", id);
        Boleta boleta = obtenerBoletaPorId(id);
        if (boleta.isAnulada()) {
            log.warn("Se intento anular una boleta ya anulada. id={}", id);
            throw new ReglaNegocioException("La boleta ya se encuentra anulada.");
        }
        boleta.setAnulada(true);
        boleta.setFechaAnulacion(LocalDateTime.now());
        boletaRepository.save(boleta);
        log.info("Boleta anulada exitosamente. id={}, folio={}", boleta.getId(), boleta.getFolio());
    }

    private CalculoBoleta calcularBoleta(String runCliente, List<BoletaProductoRequestDTO> productosRequestDTO) {
        if (runCliente == null || runCliente.isBlank()) {
            log.warn("Se intento calcular una boleta sin RUN de cliente.");
            throw new RecursoNuloException("Debe ingresar el RUN del cliente.");
        }
        if (productosRequestDTO == null || productosRequestDTO.isEmpty()) {
            log.warn("Se intento calcular una boleta sin productos. runCliente={}", runCliente);
            throw new ReglaNegocioException("Debe agregar al menos un producto a la boleta.");
        }

        log.info("Calculando boleta. runCliente={}, productosSolicitados={}", runCliente, productosRequestDTO.size());

        ClienteRemotoDTO clienteRemotoDTO = clienteBeneficioClient.obtenerClientePorRun(runCliente);
        Integer descuentoBeneficio = obtenerDescuentoBeneficio(clienteRemotoDTO);

        log.info("Cliente obtenido para boleta. runCliente={}, idBeneficio={}, descuentoEntero={}",
                clienteRemotoDTO.getRun(), clienteRemotoDTO.getIdBeneficio(), descuentoBeneficio);

        ClienteSnapshot clienteSnapshot = construirClienteSnapshot(clienteRemotoDTO, descuentoBeneficio);
        BigDecimal porcentajeDescuento = obtenerPorcentajeDescuento(descuentoBeneficio);
        List<BoletaDetalle> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : consolidarProductos(productosRequestDTO).entrySet()) {
            if (entry.getValue() > 10) {
                log.warn("Se intento agregar una cantidad consolidada mayor a 10 para el SKU {}: cantidad={}", entry.getKey(), entry.getValue());
                throw new ReglaNegocioException("La cantidad consolidada para el SKU " + entry.getKey() + " no puede ser mayor a 10 unidades.");
            }
            if (entry.getValue() <= 0) {
                log.warn("Se intento agregar una cantidad consolidada menor o igual a 0 para el SKU {}: cantidad={}", entry.getKey(), entry.getValue());
                throw new ReglaNegocioException("La cantidad consolidada para el SKU " + entry.getKey() + " debe ser mayor a 0.");
            }
            ProductoRemotoDTO productoRemotoDTO = inventarioClient.obtenerProductoPorSku(entry.getKey());
            validarProductoFacturable(productoRemotoDTO);
            validarReceta(clienteSnapshot.getRun(), productoRemotoDTO.getTipoReceta());

            BoletaDetalle boletaDetalle = construirDetalle(productoRemotoDTO, entry.getValue());

            subtotal = subtotal.add(boletaDetalle.getMontoLinea());
            detalles.add(boletaDetalle);
        }

        BigDecimal montoDescuento = subtotal.multiply(porcentajeDescuento).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoNeto = subtotal.subtract(montoDescuento).setScale(2, RoundingMode.HALF_UP);
        BigDecimal iva = montoNeto.multiply(IVA_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoBruto = montoNeto.add(iva).setScale(2, RoundingMode.HALF_UP);

        log.info("Boleta calculada. runCliente={}, subtotal={}, montoDescuento={}, montoNeto={}, iva={}, montoBruto={}",
                runCliente, subtotal, montoDescuento, montoNeto, iva, montoBruto);

        return new CalculoBoleta(
                clienteSnapshot,
                detalles,
                porcentajeDescuento,
                montoDescuento,
                montoNeto,
                iva,
                montoBruto);
    }

    private void aplicarResultado(Boleta boleta, CalculoBoleta resultado) {
        boleta.setCliente(resultado.clienteSnapshot());
        boleta.setPorcentajeDescuento(resultado.porcentajeDescuento());
        boleta.setMontoDescuento(resultado.montoDescuento());
        boleta.setMontoNeto(resultado.montoNeto());
        boleta.setIva(resultado.iva());
        boleta.setMontoBruto(resultado.montoBruto());
        boleta.limpiarProductos();
        for (BoletaDetalle detalle : resultado.detalles()) {
            boleta.agregarProducto(detalle);
        }
    }

    private Boleta obtenerBoletaPorId(Long id) {
        if (id == null || id <= 0) {
            log.warn("Se solicito una boleta con ID invalido. id={}", id);
            throw new RecursoNuloException("Debe ingresar un ID de boleta valido.");
        }
        return boletaRepository.findBoletaCompletaById(id)
                .orElseThrow(() -> {
                    log.warn("Boleta no encontrada. id={}", id);
                    return new RecursoNoEncontradoException("Boleta no encontrada.");
                });
    }

    private Long generarSiguienteFolio() {
        Long maxFolio = boletaRepository.findMaxFolio();
        return maxFolio + 1;
    }

    private Integer obtenerDescuentoBeneficio(ClienteRemotoDTO clienteRemotoDTO) {
        if (clienteRemotoDTO.getIdBeneficio() == null) {
            return 0;
        }
        return clienteBeneficioClient.obtenerDescuentoPorId(clienteRemotoDTO.getIdBeneficio());
    }

    private ClienteSnapshot construirClienteSnapshot(ClienteRemotoDTO clienteRemotoDTO, Integer descuentoBeneficio) {
        ClienteSnapshot clienteSnapshot = new ClienteSnapshot();
        clienteSnapshot.setRun(clienteRemotoDTO.getRun());
        clienteSnapshot.setDv(clienteRemotoDTO.getDv());
        clienteSnapshot.setNombre(clienteRemotoDTO.getNombre());
        clienteSnapshot.setApellido(clienteRemotoDTO.getApellido());
        clienteSnapshot.setCorreo(clienteRemotoDTO.getCorreo());
        clienteSnapshot.setIdBeneficio(clienteRemotoDTO.getIdBeneficio());
        clienteSnapshot.setDescuentoEntero(descuentoBeneficio != null ? descuentoBeneficio : 0);
        return clienteSnapshot;
    }

    private BigDecimal obtenerPorcentajeDescuento(Integer descuentoBeneficio) {
        if (descuentoBeneficio == null || descuentoBeneficio <= 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(descuentoBeneficio)
                .divide(CIEN, 4, RoundingMode.HALF_UP);
    }

    private BoletaDetalle construirDetalle(ProductoRemotoDTO productoRemotoDTO, int cantidad) {
        BigDecimal precioUnitario = productoRemotoDTO.getPrecio().setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoLinea = precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);

        BoletaDetalle boletaDetalle = new BoletaDetalle();
        boletaDetalle.setSkuProducto(productoRemotoDTO.getSku());
        boletaDetalle.setNombreProducto(productoRemotoDTO.getNombre());
        boletaDetalle.setTipoReceta(productoRemotoDTO.getTipoReceta());
        boletaDetalle.setCantidad(cantidad);
        boletaDetalle.setPrecioUnitario(precioUnitario);
        boletaDetalle.setMontoLinea(montoLinea);
        return boletaDetalle;
    }

    private Map<Long, Integer> consolidarProductos(List<BoletaProductoRequestDTO> productosRequestDTO) {
        Map<Long, Integer> productosConsolidados = new LinkedHashMap<>();
        for (BoletaProductoRequestDTO productoRequestDTO : productosRequestDTO) {
            productosConsolidados.merge(
                    productoRequestDTO.getSku(),
                    productoRequestDTO.getCantidad(),
                    Integer::sum);
        }
        return productosConsolidados;
    }

    private void validarProductoFacturable(ProductoRemotoDTO productoRemotoDTO) {
        if (productoRemotoDTO == null) {
            log.warn("Inventario devolvio producto nulo al facturar.");
            throw new RecursoNoEncontradoException("No fue posible obtener el producto.");
        }
        if (!productoRemotoDTO.isActivo()) {
            log.warn("Se intento facturar un producto inactivo. sku={}", productoRemotoDTO.getSku());
            throw new ReglaNegocioException("El producto SKU " + productoRemotoDTO.getSku() + " se encuentra inactivo.");
        }
        if (productoRemotoDTO.getPrecio() == null) {
            log.warn("Se intento facturar un producto sin precio. sku={}", productoRemotoDTO.getSku());
            throw new ReglaNegocioException(
                    "El producto SKU " + productoRemotoDTO.getSku() + " no posee precio configurado.");
        }
        if (productoRemotoDTO.getTipoReceta() == null || productoRemotoDTO.getTipoReceta().isBlank()) {
            log.warn("Se intento facturar un producto sin tipo de receta. sku={}", productoRemotoDTO.getSku());
            throw new ReglaNegocioException(
                    "El producto SKU " + productoRemotoDTO.getSku() + " no posee tipo de receta configurado.");
        }
    }

    private void validarReceta(String runCliente, String tipoReceta) {
        if ("venta libre".equalsIgnoreCase(tipoReceta.trim())) {
            return;
        }
        boolean recetaVigente = recetaClienteRepository.existsRecetaVigente(runCliente, tipoReceta, LocalDate.now());
        if (!recetaVigente) {
            log.warn("Cliente sin receta vigente para el tipo requerido. runCliente={}, tipoReceta={}",
                    runCliente, tipoReceta);
            throw new ReglaNegocioException(
                    "El cliente no posee una receta vigente para el tipo de receta requerido: " + tipoReceta + ".");
        }
    }

    private record CalculoBoleta(
            ClienteSnapshot clienteSnapshot,
            List<BoletaDetalle> detalles,
            BigDecimal porcentajeDescuento,
            BigDecimal montoDescuento,
            BigDecimal montoNeto,
            BigDecimal iva,
            BigDecimal montoBruto) {
    }
}
