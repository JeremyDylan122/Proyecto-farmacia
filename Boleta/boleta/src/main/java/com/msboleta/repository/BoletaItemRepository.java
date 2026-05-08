package com.msboleta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.msboleta.model.BoletaItem;


public interface BoletaItemRepository extends JpaRepository<BoletaItem, Long> {
    List<BoletaItem> findBySku(Long sku);
}
