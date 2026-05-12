package com.boleta.gestionboleta.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearBoletaDTO {

    @NotBlank(message = "Debe ingresar el RUN del cliente.")
    @Size(min = 8, max = 8, message = "El RUN debe tener exactamente 8 digitos.")
    @Pattern(regexp = "\\d{8}", message = "El RUN debe contener solo digitos.")
    private String runCliente;

    @Valid
    @Size(min = 1, message = "Debe agregar al menos un producto a la boleta.")
    private List<BoletaProductoRequestDTO> productos;
}
