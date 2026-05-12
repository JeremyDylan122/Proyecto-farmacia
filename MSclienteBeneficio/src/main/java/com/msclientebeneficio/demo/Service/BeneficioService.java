package com.msclientebeneficio.demo.Service;

import org.springframework.stereotype.Service;

import com.msclientebeneficio.demo.Exception.RecursoNoEncontradoException;
import com.msclientebeneficio.demo.Model.Beneficio;
import com.msclientebeneficio.demo.Repository.BeneficioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeneficioService {

    private final BeneficioRepository beneficioRepository;

    public Beneficio findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del beneficio no puede ser nulo o negativo.");
        }
        return beneficioRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Beneficio no encontrado."));
    }

    public Integer obtenerDescuentoPorId(Long id) {
        return findById(id).getDescuento();
    }

}
