package com.compra.farma.service;

import org.springframework.stereotype.Service;

import com.compra.farma.dto.DetalleMapper;
import com.compra.farma.dto.DtoDetalle;
import com.compra.farma.model.ModeloDetalleFactura;
import com.compra.farma.repository.DetalleFacturaRepositorio;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioDetalle {

    private final DetalleFacturaRepositorio repoDetalle;
    private final DetalleMapper mapper;

    public List<DtoDetalle> listarPorFactura(Long facturaId) {
        return repoDetalle.findByFactura_IdFactura(facturaId).stream()
                .map(mapper::toDTO)
                .toList();
    }
    public DtoDetalle obtenerPorId (Long id){
        ModeloDetalleFactura detalle = repoDetalle.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado: " + id));
        return mapper.toDTO(detalle);
    }
    @Transactional
    public void eliminar (Long id){
        if(!repoDetalle.existsById(id)){
            throw new RuntimeException("Detalle no encontrado: " + id);
        }
        repoDetalle.deleteById(id);
    }


}
