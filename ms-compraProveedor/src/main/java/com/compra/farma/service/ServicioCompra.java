package com.compra.farma.service;

import com.compra.farma.exception.CompraNoEncontradaException;
import com.compra.farma.ProveedorClient; 
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.web.reactive.function.client.WebClient; 
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
    private final ProveedorClient proveedorClient; 
    private final WebClient.Builder webClientBuilder; 

    @Value("${microservicio.lote.url}")
    private String loteUrl;

    public ServicioCompra(RepositorioCompra repo, CompraMapper mapper, ProveedorClient proveedorClient, WebClient.Builder webClientBuilder) {
        this.repo = repo;
        this.mapper = mapper;
        this.proveedorClient = proveedorClient;
        this.webClientBuilder = webClientBuilder;
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

    public DtoCompra buscarPorId(String idStr){
        ModeloCompra compra = repo.findById(idStr)
                .orElseThrow(() -> new CompraNoEncontradaException("Compra no encontrada: " + idStr));
                
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

    public void eliminarCompra(String idStr){
        if(!repo.existsById(idStr)){
            throw new CompraNoEncontradaException("Compra no encontrada: " + idStr);
        }
        repo.deleteById(idStr);
    }

    public Object enviarDatosALote(Object datosDelLote) {
        WebClient webClient = webClientBuilder.baseUrl(loteUrl).build();
        
        try {
            return webClient.post()
                    .uri("/api/v1/inventario/compras") 
                    .bodyValue(datosDelLote) 
                    .retrieve()
                    .bodyToMono(Object.class) 
                    .block();
        } catch (Exception e) {
            System.err.println("Error al conectar con el microservicio Gestion/Lote: " + e.getMessage());
            throw new RuntimeException("No se pudo registrar el lote en el ecosistema.");
        }
    }
}