package com.farmacia.proy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProveedorResponseDto {

    private String rutProveedor;
    private String nombre;
    private String direccion;
    private String telefono;    
    private String email;

}
