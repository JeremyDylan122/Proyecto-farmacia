package com.compra.farma.dto;

import com.compra.farma.model.ModeloCompra;
import org.springframework.stereotype.Component;

@Component
public class CompraMapper {

    public ModeloCompra toEntity(DtoCompra dto) {
        return new ModeloCompra(
            dto.idOrdenCompra(),
            dto.rutProveedor(),
            dto.sku(),
            dto.cantidad(),
            dto.totalCompra()
        );
    }

    public DtoCompra toDTO(ModeloCompra entity){
        return new DtoCompra(
            entity.getIdOrdenCompra(),
            entity.getRutProveedor(),
            entity.getSku(),
            entity.getCantidad(),
            entity.getTotalCompra()
        );
    }

}
