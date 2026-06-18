package com.inventario.msinventario.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.msinventario.model.Lote;

@Repository
public interface LoteRepository extends JpaRepository<Lote, String> {

    List<Lote> findByStockSkuAndActivoTrueOrderByFechaVencimientoAsc(Long sku);

}
