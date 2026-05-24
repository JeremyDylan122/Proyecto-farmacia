package com.compra.farma.service;

import com.compra.farma.exception.CompraNoEncontradaException;
import com.compra.farma.ProveedorClient; 
import org.springframework.stereotype.Service;
import com.compra.farma.dto.DtoCompra;
import com.compra.farma.dto.CompraMapper;
import com.compra.farma.model.ModeloCompra;
import com.compra.farma.repository.RepositorioCompra;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioCompra {

    private final RepositorioCompra repo;
    private final CompraMapper mapper;  
    private final ProveedorClient proveedorClient; // <-- 1. Declaramos el cliente Feign

    // 2. Lo agregamos al constructor para mantener tu excelente práctica de inyección
    public ServicioCompra(RepositorioCompra repo, CompraMapper mapper, ProveedorClient proveedorClient) {
        this.repo = repo;
        this.mapper = mapper;
        this.proveedorClient = proveedorClient;
    }

    public DtoCompra crear(DtoCompra dto){
        ModeloCompra entidad = mapper.toEntity(dto);
        ModeloCompra guardado = repo.save(entidad);
        return mapper.toDTO(guardado);
    }

    public List<DtoCompra> listaCompras(){
        return repo.findAll().stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toList());
    }

    public DtoCompra buscarPorId(Long id){
        // 3. Modificamos este método para que busque la compra primero
        ModeloCompra compra = repo.findById(id)
                .orElseThrow(() -> new CompraNoEncontradaException("Compra no encontrada: " + id));
                
        DtoCompra dtoSinProveedor = mapper.toDTO(compra);

        Object datosProveedor;
        try {
            datosProveedor = proveedorClient.obtenerProveedorPorRut(compra.getRutProveedor());
        } catch (Exception e) {
            datosProveedor = "Información del proveedor no disponible temporalmente.";
        }

        return new DtoCompra(
                dtoSinProveedor.idOrdenCompra(),
                dtoSinProveedor.rutProveedor(),
                dtoSinProveedor.sku(),
                dtoSinProveedor.cantidad(),
                dtoSinProveedor.totalCompra(),
                dtoSinProveedor.codigoLote(),
                dtoSinProveedor.fechaVencimiento(),
                dtoSinProveedor.factura(),
                datosProveedor 
        );
    }

    public void eliminarCompra(Long id){
        if(!repo.existsById(id)){
            throw new CompraNoEncontradaException("Compra no encontrada: " + id);
        }
        repo.deleteById(id);
    }
}