package com.inventario.msinventario.dto.stockDTOs;

import java.util.List;

import com.inventario.msinventario.dto.loteDTOs.LoteDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {

    @Schema(description="Identificador único de producto", example="780003")
    @NotNull(message = "El SKU es obligatorio.")
    @Positive(message = "El SKU debe ser un número válido positivo.")
    private Long sku;

    @Schema(description="Cantidad total.", example="50")
    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 0, message = "La cantidad mínima no puede ser negativa.") 
    private Integer cantidadTotal;

    @Schema(description="Ubicacion en bodega.", example="A-02-1-03")
    private String ubicacionBodega;

    @Schema(description = "Listado de lotes asociados.", accessMode = Schema.AccessMode.READ_ONLY)
    private List<LoteDTO> lotes;

}
