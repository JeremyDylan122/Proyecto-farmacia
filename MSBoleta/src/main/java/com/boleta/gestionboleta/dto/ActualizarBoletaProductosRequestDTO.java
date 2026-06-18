package com.boleta.gestionboleta.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos para actualizar el listado de productos de una boleta existente")
public class ActualizarBoletaProductosRequestDTO {

    @Valid
    @Size(min = 1, message = "Debe mantener al menos un producto en la boleta.")
    @Schema(description = "Nuevo listado de productos de la boleta (mínimo 1)", required = true)
    private List<BoletaProductoRequestDTO> productos;
}
