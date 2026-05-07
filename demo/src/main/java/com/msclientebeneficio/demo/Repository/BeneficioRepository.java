package com.msclientebeneficio.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.msclientebeneficio.demo.Model.Beneficio;

@Repository
public interface  BeneficioRepository extends JpaRepository<Beneficio, Long> {

    Optional<Beneficio> findById(Long id);
}
