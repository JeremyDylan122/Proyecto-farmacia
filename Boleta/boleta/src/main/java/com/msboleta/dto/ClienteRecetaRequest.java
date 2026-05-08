package com.msboleta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteRecetaRequest {
    @NotBlank(message = "El tipo de receta es obligatorio.")
    private String tipoReceta;
}
