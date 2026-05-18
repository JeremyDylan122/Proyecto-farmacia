package com.compra.farma.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compra.farma.model.ModeloDetalleFactura;

@Repository
public interface DetalleFacturaRepositorio extends JpaRepository<ModeloDetalleFactura, Long> {

    List<ModeloDetalleFactura> findByFactura_IdFactura(Long facturaId);

}
