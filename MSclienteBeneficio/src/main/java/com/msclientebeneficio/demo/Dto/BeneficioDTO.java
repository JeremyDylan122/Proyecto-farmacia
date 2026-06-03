package com.msclientebeneficio.demo.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Información de un beneficio de descuento")
public class BeneficioDTO {

    @NotBlank(message = "El id no puede estar vacío.")
    @NotNull(message = "El id no puede ser nulo.")
    @Schema(description = "Identificador único del beneficio", example = "1", required = true)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @NotNull(message = "El nombre no puede ser nulo.")
    @Schema(description = "Nombre del convenio o tipo de beneficio", example = "Fonasa", required = true)
    private String nombre;

    @NotBlank(message = "El descuento no puede estar vacío.")
    @NotNull(message = "El descuento no puede ser nulo.")
    @Schema(description = "Porcentaje de descuento asociado (entero de 0 a 99)", example = "15", required = true, minimum = "0", maximum = "99")
    private Integer descuento;
}
