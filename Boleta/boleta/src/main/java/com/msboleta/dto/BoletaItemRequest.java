package com.msboleta.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BoletaItemRequest {
    @NotNull(message = "El SKU es obligatorio.")
    private Long sku;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad minima por producto es 1.")
    private Integer cantidad;
}