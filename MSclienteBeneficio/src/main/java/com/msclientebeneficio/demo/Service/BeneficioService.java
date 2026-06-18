package com.msclientebeneficio.demo.Service;

import org.springframework.stereotype.Service;

import com.msclientebeneficio.demo.Exception.RecursoNoEncontradoException;
import com.msclientebeneficio.demo.Model.Beneficio;
import com.msclientebeneficio.demo.Repository.BeneficioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficioService {

    private final BeneficioRepository beneficioRepository;

    public Beneficio findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("Se solicito un beneficio con ID invalido. id={}", id);
            throw new IllegalArgumentException("El ID del beneficio no puede ser nulo o negativo.");
        }
        log.info("Buscando beneficio por ID. id={}", id);
        return beneficioRepository.findById(id).orElseThrow(() -> {
            log.warn("Beneficio no encontrado. id={}", id);
            return new RecursoNoEncontradoException("Beneficio no encontrado.");
        });
    }

    public Integer obtenerDescuentoPorId(Long id) {
        Integer descuento = findById(id).getDescuento();
        log.info("Descuento obtenido para beneficio. id={}, descuento={}", id, descuento);
        return descuento;
    }
}
