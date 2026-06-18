package com.inventario.msinventario.dto.ventaDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos requeridos por el inventario para procesar venta y rebaja de stock.")
public class VentaRequestDTO {
    
    @Schema(description="Identificador único de producto", example="780001")
    @NotNull(message = "El SKU es obligatorio.")
    private Long sku;

    @Schema(description = "Cantidad total de unidades vendidas que descontarán al stock.", example = "6")
    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad minima de venta es 1.")
    private Integer cantidad;

    @Schema(description = "Identificador único de la orden de venta.", example = "10245")
    @NotNull(message = "El ID de venta es obligatorio.")
    private Long idVenta; 

}
