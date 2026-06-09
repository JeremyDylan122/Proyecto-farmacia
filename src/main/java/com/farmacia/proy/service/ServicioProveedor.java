package com.farmacia.proy.service;

import com.farmacia.proy.Exceptions.ProveedorNotFoundException;
import com.farmacia.proy.dto.ProveedorMapper;
import com.farmacia.proy.dto.ProveedorRequestDto;
import com.farmacia.proy.dto.ProveedorResponseDto;
import com.farmacia.proy.model.Proveedor;
import com.farmacia.proy.repository.RepositorioProveedor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; 
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioProveedor {

    private final RepositorioProveedor repositorio;
    
    private final WebClient.Builder webClientBuilder;

    @Value("${microservicio.farma.url}")
    private String farmaUrl;

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

    public boolean verificarMedicamentoEnFarma(Long medicamentoId) {
        WebClient webClient = webClientBuilder.baseUrl(farmaUrl).build();
        try {
            Boolean existe = webClient.get()
                    .uri("/medicamentos/existe/" + medicamentoId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block(); 
            
            return existe != null && existe;
        } catch (Exception e) {
            System.err.println("No se pudo conectar con el microservicio farma (8086): " + e.getMessage());
            return false;
        }
    }
}
