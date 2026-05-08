package com.msboleta.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BoletaCreateRequest {
    @NotBlank(message = "El RUN del cliente es obligatorio.")
    private String runCliente;

    @NotEmpty(message = "Debe incluir al menos un producto en la boleta.")
    @Valid
    private List<BoletaItemRequest> productos;
}