package com.farmacia.proy.dto;

import com.farmacia.proy.model.Proveedor;

public class ProveedorMapper {

    public static ProveedorResponseDto toDto(Proveedor entity) {
        return new ProveedorResponseDto(
                entity.getRutProveedor(),
                entity.getNombre(),
                entity.getDireccion(),
                entity.getTelefono(),
                entity.getEmail()
        );
    }

    public static Proveedor toEntity(ProveedorRequestDto requestDto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setRutProveedor(requestDto.getRutProveedor());
        proveedor.setNombre(requestDto.getNombre());
        proveedor.setDireccion(requestDto.getDireccion());
        proveedor.setTelefono(requestDto.getTelefono());
        proveedor.setEmail(requestDto.getEmail());
        return proveedor;
    }   

}
