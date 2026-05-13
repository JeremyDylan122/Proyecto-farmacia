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
import com.boleta.gestionboleta.dto.BoletaResponseDTOMapper;
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

@Service
@RequiredArgsConstructor
@Transactional
public class BoletaService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final BigDecimal IVA_RATE = new BigDecimal("0.19");

    private final BoletaRepository boletaRepository;
    private final RecetaClienteRepository recetaClienteRepository;
    private final BoletaResponseDTOMapper boletaResponseDTOMapper;
    private final ClienteBeneficioClient clienteBeneficioClient;
    private final InventarioClient inventarioClient;

    public BoletaResponseDTO crearBoleta(CrearBoletaRequestDTO crearBoletaRequestDTO) {
        if (crearBoletaRequestDTO == null) {
            throw new RecursoNuloException("La boleta no puede ser nula.");
        }

        ResultadoCalculoBoleta resultado = calcularBoleta(
                crearBoletaRequestDTO.getRunCliente(),
                crearBoletaRequestDTO.getProductos());

        Boleta boleta = new Boleta();
        boleta.setFolio(generarSiguienteFolio());
        boleta.setFechaEmision(LocalDateTime.now());
        boleta.setAnulada(false);
        aplicarResultado(boleta, resultado);

        Boleta boletaGuardada = boletaRepository.save(boleta);
        return boletaResponseDTOMapper.toDTO(boletaGuardada);
    }

    public BoletaResponseDTO buscarPorId(Long id) {
        return boletaResponseDTOMapper.toDTO(obtenerBoletaPorId(id));
    }

    public List<BoletaResponseDTO> listarPorRunCliente(String runCliente) {
        return boletaRepository.findByClienteRunOrderByFechaEmisionDesc(runCliente)
                .stream()
                .map(boletaResponseDTOMapper::toDTO)
                .toList();
    }

    public List<BoletaResponseDTO> listarPorSkuProducto(Long skuProducto) {
        return boletaRepository.findBySkuProducto(skuProducto)
                .stream()
                .map(boletaResponseDTOMapper::toDTO)
                .toList();
    }

    public BoletaResponseDTO actualizarProductos(Long id, ActualizarBoletaProductosRequestDTO actualizarBoletaProductosRequestDTO) {
        if (actualizarBoletaProductosRequestDTO == null) {
            throw new RecursoNuloException("La actualizacion de productos no puede ser nula.");
        }

        Boleta boleta = obtenerBoletaPorId(id);
        if (boleta.isAnulada()) {
            throw new ReglaNegocioException("No es posible editar una boleta anulada.");
        }

        ResultadoCalculoBoleta resultado = calcularBoleta(
                boleta.getCliente().getRun(),
                actualizarBoletaProductosRequestDTO.getProductos());

        aplicarResultado(boleta, resultado);
        Boleta boletaActualizada = boletaRepository.save(boleta);
        return boletaResponseDTOMapper.toDTO(boletaActualizada);
    }

    public void anularBoleta(Long id) {
        Boleta boleta = obtenerBoletaPorId(id);
        if (boleta.isAnulada()) {
            throw new ReglaNegocioException("La boleta ya se encuentra anulada.");
        }
        boleta.setAnulada(true);
        boleta.setFechaAnulacion(LocalDateTime.now());
        boletaRepository.save(boleta);
    }

    private ResultadoCalculoBoleta calcularBoleta(String runCliente, List<BoletaProductoRequestDTO> productosRequestDTO) {
        if (runCliente == null || runCliente.isBlank()) {
            throw new RecursoNuloException("Debe ingresar el RUN del cliente.");
        }
        if (productosRequestDTO == null || productosRequestDTO.isEmpty()) {
            throw new ReglaNegocioException("Debe agregar al menos un producto a la boleta.");
        }

        ClienteRemotoDTO clienteRemotoDTO = clienteBeneficioClient.obtenerClientePorRun(runCliente);
        Integer descuentoBeneficio = 0;
        if (clienteRemotoDTO.getIdBeneficio() != null) {
            descuentoBeneficio = clienteBeneficioClient.obtenerDescuentoPorId(clienteRemotoDTO.getIdBeneficio());
        }

        ClienteSnapshot clienteSnapshot = construirClienteSnapshot(clienteRemotoDTO, descuentoBeneficio);
        BigDecimal porcentajeDescuento = obtenerPorcentajeDescuento(descuentoBeneficio);
        List<BoletaDetalle> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : consolidarProductos(productosRequestDTO).entrySet()) {
            ProductoRemotoDTO productoRemotoDTO = inventarioClient.obtenerProductoPorSku(entry.getKey());
            validarProductoFacturable(productoRemotoDTO);
            validarReceta(clienteSnapshot.getRun(), productoRemotoDTO.getTipoReceta());

            int cantidad = entry.getValue();
            BigDecimal precioUnitario = productoRemotoDTO.getPrecio().setScale(2, RoundingMode.HALF_UP);
            BigDecimal montoLinea = precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);

            BoletaDetalle boletaDetalle = new BoletaDetalle();
            boletaDetalle.setSkuProducto(productoRemotoDTO.getSku());
            boletaDetalle.setNombreProducto(productoRemotoDTO.getNombre());
            boletaDetalle.setTipoReceta(productoRemotoDTO.getTipoReceta());
            boletaDetalle.setCantidad(cantidad);
            boletaDetalle.setPrecioUnitario(precioUnitario);
            boletaDetalle.setMontoLinea(montoLinea);

            subtotal = subtotal.add(montoLinea);
            detalles.add(boletaDetalle);
        }

        BigDecimal montoDescuento = subtotal.multiply(porcentajeDescuento).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoNeto = subtotal.subtract(montoDescuento).setScale(2, RoundingMode.HALF_UP);
        BigDecimal iva = montoNeto.multiply(IVA_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoBruto = montoNeto.add(iva).setScale(2, RoundingMode.HALF_UP);

        return new ResultadoCalculoBoleta(
                clienteSnapshot,
                detalles,
                porcentajeDescuento,
                montoDescuento,
                montoNeto,
                iva,
                montoBruto);
    }

    private void aplicarResultado(Boleta boleta, ResultadoCalculoBoleta resultado) {
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
            throw new RecursoNuloException("Debe ingresar un ID de boleta valido.");
        }
        return boletaRepository.findBoletaCompletaById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Boleta no encontrada."));
    }

    private Long generarSiguienteFolio() {
        Long maxFolio = boletaRepository.findMaxFolio();
        return maxFolio + 1;
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
            throw new RecursoNoEncontradoException("No fue posible obtener el producto.");
        }
        if (!productoRemotoDTO.isActivo()) {
            throw new ReglaNegocioException("El producto SKU " + productoRemotoDTO.getSku() + " se encuentra inactivo.");
        }
        if (productoRemotoDTO.getPrecio() == null) {
            throw new ReglaNegocioException("El producto SKU " + productoRemotoDTO.getSku() + " no posee precio configurado.");
        }
        if (productoRemotoDTO.getTipoReceta() == null || productoRemotoDTO.getTipoReceta().isBlank()) {
            throw new ReglaNegocioException("El producto SKU " + productoRemotoDTO.getSku() + " no posee tipo de receta configurado.");
        }
    }

    private void validarReceta(String runCliente, String tipoReceta) {
        if ("venta libre".equalsIgnoreCase(tipoReceta.trim())) {
            return;
        }
        boolean recetaVigente = recetaClienteRepository.existsRecetaVigente(runCliente, tipoReceta, LocalDate.now());
        if (!recetaVigente) {
            throw new ReglaNegocioException(
                    "El cliente no posee una receta vigente para el tipo de receta requerido: " + tipoReceta + ".");
        }
    }

    private record ResultadoCalculoBoleta(
            ClienteSnapshot clienteSnapshot,
            List<BoletaDetalle> detalles,
            BigDecimal porcentajeDescuento,
            BigDecimal montoDescuento,
            BigDecimal montoNeto,
            BigDecimal iva,
            BigDecimal montoBruto) {
    }
}
