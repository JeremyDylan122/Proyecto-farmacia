package com.boleta.gestionboleta.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para registrar la receta médica de un cliente")
public class RecetaClienteRequestDTO {

    @NotBlank(message = "Debe ingresar el RUN del cliente.")
    @Size(min = 8, max = 8, message = "El RUN debe tener exactamente 8 digitos.")
    @Pattern(regexp = "\\d{8}", message = "El RUN debe contener solo digitos.")
    @Schema(description = "RUN del cliente al que pertenece la receta (8 dígitos)", example = "12345678", required = true, minLength = 8, maxLength = 8, pattern = "\\d{8}")
    private String runCliente;

    @NotBlank(message = "Debe ingresar el tipo de receta.")
    @Schema(description = "Tipo de receta (ej. Receta Simple, Retenida, Cheque)", example = "Receta Simple", required = true)
    private String tipoReceta;

    @NotBlank(message = "Debe ingresar el folio de la receta.")
    @Schema(description = "Folio único de identificación de la receta física", example = "REC-98765", required = true)
    private String folioReceta;

    @NotNull(message = "Debe ingresar la fecha de emision de la receta.")
    @Schema(description = "Fecha en que se emitió la receta por el profesional de salud", example = "2026-06-01", required = true)
    private LocalDate fechaEmision;

    @Schema(description = "Fecha de vencimiento de la receta", example = "2026-12-01")
    private LocalDate fechaVencimiento;
}
