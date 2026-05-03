package com.farmacia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProveedorDto(

    @NotBlank(message = "El rut del proveedor es obligatorio")
    String rutProveedor,

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    String nombre,

    
    String direccion,

    @Pattern(regexp = "^(\\d{9}|\\d{12})$", message = "El teléfono debe tener 9 o 12 digitos")
    String telefono,

    @Email(message = "El email debe ser valido")
    String email

) {

}
