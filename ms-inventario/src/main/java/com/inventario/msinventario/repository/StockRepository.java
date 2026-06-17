package com.inventario.msinventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.msinventario.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>{

}
    