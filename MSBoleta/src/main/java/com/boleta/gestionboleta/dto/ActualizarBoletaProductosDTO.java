package com.boleta.gestionboleta.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarBoletaProductosDTO {

    @Valid
    @Size(min = 1, message = "Debe mantener al menos un producto en la boleta.")
    private List<BoletaProductoRequestDTO> productos;
}
