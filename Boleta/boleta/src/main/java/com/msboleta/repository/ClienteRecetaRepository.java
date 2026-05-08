package com.msboleta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.msboleta.model.ClienteReceta;


public interface ClienteRecetaRepository extends JpaRepository<ClienteReceta, Long> {
    List<ClienteReceta> findByRunCliente(String runCliente);
    boolean existsByRunClienteAndTipoRecetaIgnoreCase(String runCliente, String tipoReceta);
}