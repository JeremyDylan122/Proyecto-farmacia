package com.farmacia.proy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "DTO que representa la estructura de datos para un Proveedor")
public record ProveedorDto(

    @Schema(
        description = "RUT único del proveedor (formato de 12 caracteres incluyendo puntos y guión)", 
        example = "12.345.678-9", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "El rut del proveedor es obligatorio")
    @Pattern(regexp = "^(\\d{12})$", message = "El rut debe tener 12 digitos con puntos y guion")
    String rutProveedor,

    @Schema(
        description = "Nombre comercial o razón social del proveedor", 
        example = "Laboratorio Chile S.A.", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "El nombre del proveedor es obligatorio")
    String nombre,

    @Schema(
        description = "Dirección física de la casa matriz o sucursal", 
        example = "Av. Vitacura 1234, Santiago"
    )
    String direccion,

    @Schema(
        description = "Teléfono de contacto (9 dígitos para celular o 12 dígitos con código de país)", 
        example = "912345678"
    )
    @Pattern(regexp = "^(\\d{9}|\\d{12})$", message = "El teléfono debe tener 9 o 12 digitos")
    String telefono,

    @Schema(
        description = "Correo electrónico de contacto del proveedor", 
        example = "contacto@labchile.cl"
    )
    @Email(message = "El email debe ser valido")
    String email

) {
}