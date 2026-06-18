package com.compra.farma.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.compra.farma.dto.DtoFactura;
import com.compra.farma.dto.FacturaMapper;
import com.compra.farma.dto.DetalleMapper;
import com.compra.farma.model.ModeloFactura;
import com.compra.farma.model.ModeloDetalleFactura;
import com.compra.farma.model.ModeloCompra;
import com.compra.farma.repository.RepositorioCompra;
import com.compra.farma.repository.ModeloFacturaRepositorio;
import com.compra.farma.exception.CompraNoEncontradaException;

@Service
public class ServicioFactura {

    private final ModeloFacturaRepositorio facturaRepo;
    private final RepositorioCompra compraRepo;
    private final DetalleMapper detalleMapper;
    private final FacturaMapper facturaMapper;
    
    public ServicioFactura(ModeloFacturaRepositorio facturaRepo, RepositorioCompra compraRepo, DetalleMapper detalleMapper, FacturaMapper facturaMapper) {
        this.facturaRepo = facturaRepo;
        this.compraRepo = compraRepo;
        this.detalleMapper = detalleMapper;
        this.facturaMapper = facturaMapper;
    }

    @Transactional
    public DtoFactura crearFactura(DtoFactura dto) {
        ModeloFactura facturaEntity = facturaMapper.toEntity(dto);
        if (dto.getDetalles() != null) {
            List<ModeloDetalleFactura> detallesEntity = dto.getDetalles().stream()
                .map(detDto -> {
                    ModeloDetalleFactura detalleEntity = detalleMapper.toEntity(detDto);
                    ModeloCompra compra = compraRepo.findById(detDto.getIdOrdenCompra())
                        .orElseThrow(() -> new CompraNoEncontradaException("Compra no encontrada: " + detDto.getIdOrdenCompra()));
                    detalleEntity.setFactura(facturaEntity);
                    detalleEntity.setCompra(compra);
                    return detalleEntity;
                })
                .collect(Collectors.toList());
            facturaEntity.setDetalles(detallesEntity);
        }
        ModeloFactura facturaGuardada = facturaRepo.save(facturaEntity);
        return facturaMapper.toDTO(facturaGuardada);
    }
    public List<DtoFactura> listarFacturas() {
        return facturaRepo.findAll().stream()
                .map(facturaMapper::toDTO)
                .collect(Collectors.toList());
    }
    public DtoFactura buscarFacturaPorId(Long id) {
        return facturaRepo.findById(id)
                .map(facturaMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + id));
    }
    public void eliminarFactura(Long id) {
        if (!facturaRepo.existsById(id)) {
            throw new RuntimeException("Factura no encontrada: " + id);
        }
        facturaRepo.deleteById(id);
    }

}
