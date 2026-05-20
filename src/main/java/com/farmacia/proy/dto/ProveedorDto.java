package com.farmacia.proy.dto;

import jakarta.validation.constraints.*;

public record ProveedorDto(

    @NotBlank(message = "El rut del proveedor es obligatorio")
    @Pattern(regexp = "^(\\d{12})$", message = "El rut debe tener 12 digitos con puntos y guion")
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
