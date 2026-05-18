package com.compra.farma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compra.farma.model.ModeloFactura;

@Repository
public interface ModeloFacturaRepositorio extends JpaRepository<ModeloFactura, Long> {

}
