package com.msclientebeneficio.demo.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BeneficioDTO {
    @NotBlank(message = "El id no puede estar vacío.")
    @NotNull(message = "El id no puede ser nulo.")
    private Long id;
    @NotBlank(message = "El nombre no puede estar vacío.")
    @NotNull(message = "El nombre no puede ser nulo.")
    private String nombre;
    @NotBlank(message = "El descuento no puede estar vacío.")
    @NotNull(message = "El descuento no puede ser nulo.")
    private Integer descuento;
}
