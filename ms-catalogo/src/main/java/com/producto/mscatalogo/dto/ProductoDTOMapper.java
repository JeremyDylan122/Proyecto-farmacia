package com.producto.mscatalogo.dto;

import org.springframework.stereotype.Component;

import com.producto.mscatalogo.model.Producto;

@Component
public class ProductoDTOMapper {


    public ProductoDTO toDTO(Producto producto){
        if (producto == null) {
            return null;            
        }
        ProductoDTO dto = new ProductoDTO();
        dto.setSku(producto.getSku());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setLaboratorio(producto.getLaboratorio());
        dto.setDescripcion(producto.getDescripcion());
        dto.setActivo(producto.isActivo());
        dto.setCategoria(producto.getCategoria().getNombre());
        dto.setTipoReceta(producto.getTipoReceta().getNombre());

        return dto;
    }

    public Producto toModel(ProductoDTO productoDTO){
        if (productoDTO == null){
            return null;
        }
        Producto producto = new Producto();
        producto.setSku(productoDTO.getSku());
        producto.setNombre(productoDTO.getNombre());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setLaboratorio(productoDTO.getLaboratorio());
        producto.setDescripcion(productoDTO.getDescripcion());

        return producto;
    }

}
