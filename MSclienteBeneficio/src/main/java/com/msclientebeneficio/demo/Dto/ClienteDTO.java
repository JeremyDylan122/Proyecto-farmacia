package com.msclientebeneficio.demo.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Información de un cliente")
public class ClienteDTO {

    @NotNull(message = "El run no puede ser nulo.")
    @NotBlank(message = "El run no puede estar vacío.")
    @Size(min = 8, max = 8, message = "El run debe tener exactamente 8 caracteres.")
    @Pattern(regexp = "\\d{8}", message = "El run debe contener solo dígitos.")
    @Schema(description = "RUN del cliente (sin puntos ni guion, exactamente 8 dígitos)", example = "12345678", required = true, minLength = 8, maxLength = 8, pattern = "\\d{8}")
    private String run;

    @NotNull(message = "El dígito verificador no puede ser nulo.")
    @NotBlank(message = "El dígito verificador no puede estar vacío.")
    @Size(min = 1, max = 1, message = "El dígito verificador debe tener exactamente 1 carácter.")
    @Pattern(regexp = "[0-9Kk]", message = "El dígito verificador debe ser un número del 0 al 9 o la letra K.")
    @Schema(description = "Dígito verificador del RUN", example = "9", required = true, minLength = 1, maxLength = 1, pattern = "[0-9Kk]")
    private String dv;

    @NotNull(message = "El nombre no puede ser nulo.")
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 80, message = "El nombre no puede tener más de 50 caracteres.")
    @Schema(description = "Nombres del cliente", example = "Juan", required = true, maxLength = 80)
    private String nombre;

    @NotNull(message = "El apellido no puede ser nulo.")
    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(max = 80, message = "El apellido no puede tener más de 50 caracteres.")
    @Schema(description = "Apellidos del cliente", example = "Pérez", required = true, maxLength = 80)
    private String apellido;

    @NotNull(message = "El correo electrónico no puede ser nulo.")
    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Schema(description = "Correo electrónico de contacto", example = "juan.perez@email.com", required = true)
    private String correo;

    @NotNull(message = "El teléfono no puede ser nulo.")
    @NotBlank(message = "El teléfono no puede estar vacío.")
    @Size(max = 9, message = "El teléfono no puede tener más de 9 caracteres.")
    @Schema(description = "Número telefónico del cliente", example = "987654321", required = true, maxLength = 9)
    private String telefono;

    @Schema(description = "Identificador del beneficio asociado", example = "1")
    private Long idBeneficio;
}
