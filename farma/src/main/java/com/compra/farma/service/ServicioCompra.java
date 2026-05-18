package com.compra.farma.service;

import com.compra.farma.exception.CompraNoEncontradaException;
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

    public ServicioCompra(RepositorioCompra repo, CompraMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
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
        return repo.findById(id)
                    .map(mapper::toDTO)
                    .orElseThrow(()-> new CompraNoEncontradaException("Compra no encontrada: " + id));
    }

    public void eliminarCompra(Long id){
        if(!repo.existsById(id)){
            throw new CompraNoEncontradaException("Compra no encontrada: " + id);
        }
        repo.deleteById(id);
    }

}
