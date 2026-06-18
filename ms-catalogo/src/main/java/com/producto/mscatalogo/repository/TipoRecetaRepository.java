package com.producto.mscatalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.producto.mscatalogo.model.TipoReceta;

public interface TipoRecetaRepository extends JpaRepository<TipoReceta, Integer>{

    TipoReceta findByNombreIgnoreCase(String nombre);

}
