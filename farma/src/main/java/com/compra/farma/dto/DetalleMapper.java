package com.compra.farma.dto;

import org.springframework.stereotype.Component;
import com.compra.farma.model.ModeloDetalleFactura;

@Component
public class DetalleMapper {

    public ModeloDetalleFactura toEntity(DtoDetalle dto) {
        ModeloDetalleFactura entity = new ModeloDetalleFactura();
        
        entity.setCantidad(dto.getCantidad());
        entity.setPrecioUnitario(dto.getPrecioUnitario());

        return entity;
    }

    public DtoDetalle toDTO(ModeloDetalleFactura entity) {
        DtoDetalle dto = new DtoDetalle();
        
        dto.setCantidad(entity.getCantidad());
        dto.setPrecioUnitario(entity.getPrecioUnitario());
        
        if(entity.getCompra() != null) {
            dto.setIdOrdenCompra(entity.getCompra().getIdOrdenCompra());
        }
        return dto;
    }

}
