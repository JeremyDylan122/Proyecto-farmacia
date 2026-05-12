package com.boleta.gestionboleta.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecetaClienteRequestDTO {

    @NotBlank(message = "Debe ingresar el RUN del cliente.")
    @Size(min = 8, max = 8, message = "El RUN debe tener exactamente 8 digitos.")
    @Pattern(regexp = "\\d{8}", message = "El RUN debe contener solo digitos.")
    private String runCliente;

    @NotBlank(message = "Debe ingresar el tipo de receta.")
    private String tipoReceta;

    @NotBlank(message = "Debe ingresar el folio de la receta.")
    private String folioReceta;

    @NotNull(message = "Debe ingresar la fecha de emision de la receta.")
    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;
}
