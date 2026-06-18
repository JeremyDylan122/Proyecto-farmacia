package com.inventario.msinventario.dto.loteDTOs;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
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
@Schema(description="Datos de un lote.")
public class LoteDTO {

    @Schema(description="Identificador único de lote.", example="LT-2026-001")
    @NotBlank(message = "El código de lote es obligatorio para el operario.")
    private String codigoLote;

    @Schema(description="Cantidad a registrar.", example="20")
    @NotNull(message = "La cantidad del lote es obligatoria.")
    @Min(value = 1, message = "La cantidad ingresada en el lote debe ser igual o mayor a 1.")
    private Integer cantidad;

    @Schema(description="Fecha de vencimiento.", example="2030-06-24")
    @NotNull(message = "La fecha de vencimiento es obligatoria.")
    @Future(message = "La fecha de vencimiento debe ser una fecha futura.")
    private LocalDate fechaVencimiento;

    @Schema(description="Identificador único de compra.", example="123456789")
    @NotNull(message = "El lote debe estar asociado sí o sí a un ID de orden de compra.")
    private Long idCompra;

    /*@Schema(description="Activo o desactivo", example="true")
    private boolean activo; // TRUE POR DEFECTO. FALSE AUTOMATICO AL LLEGAR A 0.*/

    @Schema(description="Identificador único de producto", example="780001")
    @NotNull(message = "El SKU del producto asociado es obligatorio.")
    private Long sku;

}
