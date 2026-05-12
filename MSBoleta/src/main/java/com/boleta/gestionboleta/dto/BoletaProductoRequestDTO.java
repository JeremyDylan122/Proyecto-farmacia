package com.boleta.gestionboleta.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BoletaProductoRequestDTO {

    @NotNull(message = "Debe ingresar un SKU de producto.")
    private Long sku;

    @NotNull(message = "Debe ingresar la cantidad del producto.")
    @Min(value = 1, message = "La cantidad minima por producto es 1.")
    private Integer cantidad = 1;
}
