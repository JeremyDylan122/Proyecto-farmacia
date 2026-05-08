package com.msboleta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.msboleta.model.Boleta;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    List<Boleta> findByRunCliente(String runCliente);
}