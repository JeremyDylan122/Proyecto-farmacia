package com.farmacia.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.farmacia.model.Proveedor;

@Repository
public interface RepositorioProveedor extends JpaRepository<Proveedor, String> { 

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre); 

    List<Proveedor> findByDireccionContaining(String direccion); 

    Optional<Proveedor> findByEmail(String email);

    boolean existsByEmail(String email); 

}
