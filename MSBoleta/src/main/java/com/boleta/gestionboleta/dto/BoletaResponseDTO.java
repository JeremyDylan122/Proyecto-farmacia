package com.boleta.gestionboleta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.boleta.gestionboleta.model.Boleta;
import com.boleta.gestionboleta.model.BoletaDetalle;
import com.boleta.gestionboleta.model.ClienteSnapshot;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Detalles de respuesta de una boleta de venta emitida")
public class BoletaResponseDTO {

    @Schema(description = "Identificador único de la boleta", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Folio de la boleta de venta", example = "10001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long folio;

    @Schema(description = "Información del cliente al momento de la venta", accessMode = Schema.AccessMode.READ_ONLY)
    private ClienteDTO cliente;

    @Schema(description = "Detalle de los productos incluidos en la boleta", accessMode = Schema.AccessMode.READ_ONLY)
    private List<DetalleDTO> productos;

    @Schema(description = "Porcentaje de descuento aplicado (ej: 0.15 para 15%)", example = "0.1500", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal porcentajeDescuento;

    @Schema(description = "Monto descontado del subtotal", example = "1500.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal montoDescuento;

    @Schema(description = "Monto neto (subtotal menos descuento)", example = "8500.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal montoNeto;

    @Schema(description = "Impuesto al valor agregado calculado (19%)", example = "1615.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal iva;

    @Schema(description = "Monto bruto final a pagar (neto + iva)", example = "10115.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal montoBruto;

    @Schema(description = "Fecha y hora de emisión de la boleta", example = "2026-06-03T18:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaEmision;

    @Schema(description = "Indica si la boleta ha sido anulada", example = "false", accessMode = Schema.AccessMode.READ_ONLY)
    private boolean anulada;

    public static BoletaResponseDTO from(Boleta boleta) {
        if (boleta == null) {
            return null;
        }

        BoletaResponseDTO dto = new BoletaResponseDTO();
        dto.setId(boleta.getId());
        dto.setFolio(boleta.getFolio());
        dto.setCliente(fromCliente(boleta.getCliente()));
        dto.setProductos(boleta.getProductos().stream().map(BoletaResponseDTO::fromDetalle).toList());
        dto.setPorcentajeDescuento(boleta.getPorcentajeDescuento());
        dto.setMontoDescuento(boleta.getMontoDescuento());
        dto.setMontoNeto(boleta.getMontoNeto());
        dto.setIva(boleta.getIva());
        dto.setMontoBruto(boleta.getMontoBruto());
        dto.setFechaEmision(boleta.getFechaEmision());
        dto.setAnulada(boleta.isAnulada());
        return dto;
    }

    private static ClienteDTO fromCliente(ClienteSnapshot cliente) {
        if (cliente == null) {
            return null;
        }

        ClienteDTO dto = new ClienteDTO();
        dto.setRun(cliente.getRun());
        dto.setDv(cliente.getDv());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setCorreo(cliente.getCorreo());
        dto.setIdBeneficio(cliente.getIdBeneficio());
        dto.setDescuentoEntero(cliente.getDescuentoEntero());
        return dto;
    }

    private static DetalleDTO fromDetalle(BoletaDetalle detalle) {
        DetalleDTO dto = new DetalleDTO();
        dto.setSkuProducto(detalle.getSkuProducto());
        dto.setNombreProducto(detalle.getNombreProducto());
        dto.setTipoReceta(detalle.getTipoReceta());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setMontoLinea(detalle.getMontoLinea());
        return dto;
    }

    @Data
    @Schema(description = "Datos históricos del cliente al momento de emitir la boleta")
    public static class ClienteDTO {
        @Schema(description = "RUN del cliente", example = "12345678")
        private String run;

        @Schema(description = "Dígito verificador del RUN", example = "9")
        private String dv;

        @Schema(description = "Nombre del cliente", example = "Juan")
        private String nombre;

        @Schema(description = "Apellido del cliente", example = "Pérez")
        private String apellido;

        @Schema(description = "Correo electrónico del cliente", example = "juan.perez@email.com")
        private String correo;

        @Schema(description = "ID del beneficio aplicado", example = "2")
        private Long idBeneficio;

        @Schema(description = "Porcentaje de descuento en formato entero", example = "15")
        private Integer descuentoEntero;
    }

    @Data
    @Schema(description = "Detalle de un producto en la boleta")
    public static class DetalleDTO {
        @Schema(description = "SKU del producto vendido", example = "1000000000001")
        private Long skuProducto;

        @Schema(description = "Nombre comercial del producto", example = "Paracetamol 500mg")
        private String nombreProducto;

        @Schema(description = "Tipo de receta médica requerida por el producto", example = "venta libre")
        private String tipoReceta;

        @Schema(description = "Cantidad de unidades compradas del producto", example = "2")
        private Integer cantidad;

        @Schema(description = "Precio unitario del producto al momento de la venta", example = "5000.00")
        private BigDecimal precioUnitario;

        @Schema(description = "Monto total de la línea (precio unitario * cantidad)", example = "10000.00")
        private BigDecimal montoLinea;
    }
}
