package com.boleta.gestionboleta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.boleta.gestionboleta.model.Boleta;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    @EntityGraph(attributePaths = "productos")
    List<Boleta> findByClienteRunOrderByFechaEmisionDesc(String run);

    @EntityGraph(attributePaths = "productos")
    @Query("select b from Boleta b where b.id = :id")
    Optional<Boleta> findBoletaCompletaById(@Param("id") Long id);

    @EntityGraph(attributePaths = "productos")
    @Query("select distinct b from Boleta b join b.productos p where p.skuProducto = :sku order by b.fechaEmision desc")
    List<Boleta> findBySkuProducto(@Param("sku") Long skuProducto);

    @Query("select coalesce(max(b.folio), 0) from Boleta b")
    Long findMaxFolio();
}
