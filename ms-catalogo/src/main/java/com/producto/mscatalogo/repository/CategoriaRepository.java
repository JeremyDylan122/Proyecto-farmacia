package com.producto.mscatalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.producto.mscatalogo.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{

    Categoria findByNombreIgnoreCase(String nombre);
    Boolean existsByNombreIgnoreCase(String nombre);

}
