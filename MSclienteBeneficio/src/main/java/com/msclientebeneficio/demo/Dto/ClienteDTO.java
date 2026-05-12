package com.msclientebeneficio.demo.Dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteDTO {

    
    @NotNull(message = "El run no puede ser nulo.")
    @NotBlank(message = "El run no puede estar vacío.")
    @Size(min = 8, max = 8, message = "El run debe tener exactamente 8 caracteres.")
    @Pattern(regexp = "\\d{8}", message = "El run debe contener solo dígitos.")
    private String run;
    @NotNull(message = "El dígito verificador no puede ser nulo.")
    @NotBlank(message = "El dígito verificador no puede estar vacío.")
    @Size(min = 1, max = 1, message = "El dígito verificador debe tener exactamente 1 carácter.")
    @Pattern(regexp = "[0-9Kk]", message = "El dígito verificador debe ser un número del 0 al 9 o la letra K.")
    private String dv;
    @NotNull(message = "El nombre no puede ser nulo.")
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 80, message = "El nombre no puede tener más de 50 caracteres.")
    private String nombre;
    @NotNull(message = "El apellido no puede ser nulo.")
    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(max = 80, message = "El apellido no puede tener más de 50 caracteres.")
    private String apellido;
    @NotNull(message = "El correo electrónico no puede ser nulo.")
    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    private String correo;
    @NotNull(message = "El teléfono no puede ser nulo.")
    @NotBlank(message = "El teléfono no puede estar vacío.")
    @Size(max = 9, message = "El teléfono no puede tener más de 9 caracteres.")
    private String telefono;
    private Long idBeneficio;


}
