package com.compra.farma.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para representar una factura de compra a proveedores")
public class DtoFactura {

    @Schema(description = "ID único de la factura", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idFactura;

    @Schema(description = "Número de la factura", example = "F12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String numeroFactura;

    @Schema(description = "Fecha de emisión de la factura", example = "2024-06-01T10:00:00")
    private LocalDateTime fechaEmision;

    @NotBlank
    @Schema(description = "RUT del cliente", example = "12345678-9", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rutCliente;

    @NotBlank
    @Schema(description = "Nombre del cliente", example = "Juan Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombreCliente;

    @Schema(description = "Total de la factura", example = "150000.00")
    private BigDecimal totalFactura;

    @NotEmpty
    @Schema(description = "Lista de detalles asociados a la factura")
    private List<DtoDetalle> detalles;

}
