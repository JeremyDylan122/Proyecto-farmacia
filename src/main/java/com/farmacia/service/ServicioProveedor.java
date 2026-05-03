package com.farmacia.service;

import com.farmacia.dto.ProveedorDto;
import com.farmacia.model.Proveedor;
import com.farmacia.repository.RepositorioProveedor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ServicioProveedor {

    private final RepositorioProveedor repositorio;

    public ProveedorDto crearProveedor(ProveedorDto dto){
        if (repositorio.existsById(dto.rutProveedor())){
            throw new RuntimeException("Ya existe proveedor con este rut: " + dto.rutProveedor());

        }

        Proveedor entity = toEntity(dto);
        Proveedor guardado = repositorio.save(entity);
        return toDto(guardado);
        }

    public List<ProveedorDto> obtenerProveedores(){
        return repositorio.findAll().stream().map(this::toDto).toList();
    }

    public ProveedorDto buscarPorRut(String rutProveedor){
        Proveedor entity = repositorio.findById(rutProveedor).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con rut: " + rutProveedor));
        return toDto(entity);

    }

    public void eliminarProveedor(String rutProveedor){
        if (!repositorio.existsById(rutProveedor)){
            throw new RuntimeException("Proveedor no encontradocon el rut: " + rutProveedor);
        }
        repositorio.deleteById(rutProveedor);

    }
     private Proveedor toEntity(ProveedorDto dto){

        return new Proveedor(
            dto.rutProveedor(),
            dto.nombre(),
            dto.direccion(),
            dto.telefono(),
            dto.email()
        );
     }

     private ProveedorDto toDto(Proveedor entity){

        return new ProveedorDto(
            entity.getRutProveedor(),
            entity.getNombre(),
            entity.getDireccion(),
            entity.getTelefono(),
            entity.getEmail()

        );
     }

}
