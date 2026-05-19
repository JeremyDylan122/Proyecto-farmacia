package com.farmacia.service;

import com.farmacia.Exceptions.ProveedorNotFoundException;
import com.farmacia.dto.ProveedorMapper;
import com.farmacia.dto.ProveedorRequestDto;
import com.farmacia.dto.ProveedorResponseDto;
import com.farmacia.model.Proveedor;
import com.farmacia.repository.RepositorioProveedor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ServicioProveedor {

    private final RepositorioProveedor repositorio;

    public ProveedorResponseDto crearProveedor(ProveedorRequestDto dto){
        if (repositorio.existsById(dto.getRutProveedor())){
            throw new RuntimeException("Ya existe proveedor con este rut: " + dto.getRutProveedor());

        }
        if (repositorio.existsByEmail(dto.getEmail())){
            throw new RuntimeException("Ya existe proveedor con este email: " + dto.getEmail());
        }

        Proveedor entity = ProveedorMapper.toEntity(dto);
        Proveedor guardado = repositorio.save(entity);
        return ProveedorMapper.toDto(guardado);
        }

    public List<ProveedorResponseDto> obtenerProveedores(){
        return repositorio.findAll().stream().map(ProveedorMapper::toDto).toList();
    }

    public ProveedorResponseDto buscarPorRut(String rutProveedor){
        Proveedor entity = repositorio.findById(rutProveedor).orElseThrow(() -> new ProveedorNotFoundException(rutProveedor));
        return ProveedorMapper.toDto(entity);

    }

    public void eliminarProveedor(String rutProveedor){
        if (!repositorio.existsById(rutProveedor)){
            throw new ProveedorNotFoundException(rutProveedor);
        }
        repositorio.deleteById(rutProveedor);

    }

}
