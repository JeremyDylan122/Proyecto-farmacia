package com.compra.farma.repository;

import com.compra.farma.model.ModeloCompra;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioCompra extends JpaRepository<ModeloCompra, String> {
}