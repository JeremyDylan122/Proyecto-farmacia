package com.producto.mscatalogo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.producto.mscatalogo.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{

    Producto findBySku(Long sku);
    List<Producto> findByCategoriaNombreIgnoreCase(String categoria);
    Boolean existsBySku(Long sku);

}
