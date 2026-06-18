package com.boleta.gestionboleta.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.boleta.gestionboleta.model.RecetaCliente;

@Repository
public interface RecetaClienteRepository extends JpaRepository<RecetaCliente, Long> {

    List<RecetaCliente> findByRunClienteOrderByFechaEmisionDesc(String runCliente);

    boolean existsByRunClienteAndFolioReceta(String runCliente, String folioReceta);

    @Query("""
            select case when count(r) > 0 then true else false end
            from RecetaCliente r
            where r.runCliente = :runCliente
              and lower(r.tipoReceta) = lower(:tipoReceta)
              and r.activa = true
              and (r.fechaVencimiento is null or r.fechaVencimiento >= :fecha)
            """)
    boolean existsRecetaVigente(
            @Param("runCliente") String runCliente,
            @Param("tipoReceta") String tipoReceta,
            @Param("fecha") LocalDate fecha);
}
