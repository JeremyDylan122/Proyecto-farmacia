package com.compra.farma.dto;

import java.util.List;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.compra.farma.model.ModeloDetalleFactura;
import com.compra.farma.model.ModeloFactura;

@Component
public class FacturaMapper {

    private final DetalleMapper detalleMapper;

    public FacturaMapper(DetalleMapper detalleMapper) {
        this.detalleMapper = detalleMapper;
    }

    public ModeloFactura toEntity(DtoFactura dto) {
        ModeloFactura Entity = new ModeloFactura();
        if (dto.getDetalles() != null) {
            List<ModeloDetalleFactura> detalleEntity = dto.getDetalles().stream()
                .map(detalleMapper::toEntity)
                .collect(Collectors.toList());
            Entity.setDetalles(detalleEntity);
        }
        Entity.setNombreCliente(dto.getNombreCliente());
        Entity.setRutCliente(dto.getRutCliente());
        return Entity;
    }

public DtoFactura toDTO(ModeloFactura entity) {
    DtoFactura dto = new DtoFactura();
    if (entity.getDetalles() != null) {
        List<DtoDetalle> detalleDTO = entity.getDetalles().stream()
            .map(detalleMapper::toDTO)
            .collect(Collectors.toList());
        dto.setDetalles(detalleDTO);
    }
    dto.setNombreCliente(entity.getNombreCliente());
    dto.setRutCliente(entity.getRutCliente());
    return dto;
}
}
