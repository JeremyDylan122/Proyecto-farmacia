package com.farmacia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProveedorRequestDto {

    @NotBlank(message = "El rut del proveedor es obligatorio")
    @Pattern(regexp = "^\\d{9}|\\d{12}$", message = "El rut debe tener 9 o 12 dígitos")
    private String rutProveedor;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El telefono del proveedor es obligatoria")
    @Pattern(regexp = "^\\d{9}|\\d{12}$", message = "El teléfono debe tener 9 o 12 dígitos")
    private String telefono;

    private String direccion;

    @Email(message = "El email no tiene un formato válido")
    @NotBlank(message = "El email del proveedor es obligatorio")
    private String email;

}
