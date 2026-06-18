package com.inventario.msinventario.dto.compraDTOs;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos requeridos por el inventario para procesar el ingreso de mercadería desde ms-compra.")
public class CompraRequestDTO {

    @Schema(description = "Identificador único de la orden de compra.", example = "10245")
    @NotNull(message = "El ID de la compra es obligatorio.")
    private Long idOrdenCompra;

    @Schema(description="Identificador único de producto", example="780001")
    @NotNull(message = "El SKU es obligatorio.")
    private Long sku;

    @Schema(description = "Cantidad total de unidades adquiridas que ingresarán al stock.", example = "150")
    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad minima de compra es 1.")
    private Integer cantidad;

    @Schema(description = "Código identificador único para lote.", example = "LT-2026-001")
    @NotBlank(message = "El codigo del lote es obligatorio.")
    private String codigoLote;

    @Schema(description = "Fecha de caducidad del lote (Sujeto a regla de negocio de vida útil mínima).", example = "2029-12-31")
    @NotNull(message = "La fecha de vencimiento es obligatoria. (Formato: YYYY-MM-DD)")
    private LocalDate fechaVencimiento;

}
