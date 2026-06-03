package com.boleta.gestionboleta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos de un producto individual para agregar a la boleta")
public class BoletaProductoRequestDTO {

    @NotNull(message = "Debe ingresar un SKU de producto.")
    @Schema(description = "Código SKU único del producto", example = "1000000000001", required = true)
    private Long sku;

    @NotNull(message = "Debe ingresar la cantidad del producto.")
    @Min(value = 1, message = "La cantidad del producto debe ser mayor o igual a 1.")
    @Max(value = 10, message = "La cantidad maxima permitida por producto es 10.")
    @Schema(description = "Cantidad de unidades del producto", example = "2", required = true, minimum = "1", maximum = "10")
    private Integer cantidad = 1;
}
