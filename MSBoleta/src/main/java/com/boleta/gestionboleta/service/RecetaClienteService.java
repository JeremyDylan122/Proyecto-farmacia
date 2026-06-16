package com.boleta.gestionboleta.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.boleta.gestionboleta.dto.RecetaClienteRequestDTO;
import com.boleta.gestionboleta.dto.RecetaClienteResponseDTO;
import com.boleta.gestionboleta.excepcions.RecursoDuplicadoException;
import com.boleta.gestionboleta.excepcions.RecursoNoEncontradoException;
import com.boleta.gestionboleta.excepcions.RecursoNuloException;
import com.boleta.gestionboleta.excepcions.ReglaNegocioException;
import com.boleta.gestionboleta.model.RecetaCliente;
import com.boleta.gestionboleta.client.ClienteBeneficioClient;
import com.boleta.gestionboleta.repository.RecetaClienteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecetaClienteService {

    private final RecetaClienteRepository recetaClienteRepository;
    private final ClienteBeneficioClient clienteBeneficioClient;

    public RecetaClienteResponseDTO registrarReceta(RecetaClienteRequestDTO recetaClienteRequestDTO) {
        if (recetaClienteRequestDTO == null) {
            log.warn("Se intento registrar una receta con request nulo.");
            throw new RecursoNuloException("La receta del cliente no puede ser nula.");
        }
        log.info("Registrando receta para cliente. runCliente={}, tipoReceta={}, folio={}",
                recetaClienteRequestDTO.getRunCliente(),
                recetaClienteRequestDTO.getTipoReceta(),
                recetaClienteRequestDTO.getFolioReceta());
        
        // Validar que el cliente exista en MSclienteBeneficio antes de continuar
        clienteBeneficioClient.obtenerClientePorRun(recetaClienteRequestDTO.getRunCliente());

        if (recetaClienteRepository.existsByRunClienteAndFolioReceta(
                recetaClienteRequestDTO.getRunCliente(),
                recetaClienteRequestDTO.getFolioReceta())) {
            log.warn("Receta duplicada detectada. runCliente={}, folio={}",
                    recetaClienteRequestDTO.getRunCliente(),
                    recetaClienteRequestDTO.getFolioReceta());
            throw new RecursoDuplicadoException("Ya existe una receta registrada con ese folio para el cliente.");
        }
        if ("venta libre".equalsIgnoreCase(recetaClienteRequestDTO.getTipoReceta().trim())) {
            log.warn("Se intento registrar una receta para venta libre. runCliente={}, folio={}",
                    recetaClienteRequestDTO.getRunCliente(),
                    recetaClienteRequestDTO.getFolioReceta());
            throw new ReglaNegocioException("No es necesario registrar recetas para productos de venta libre.");
        }
        if (recetaClienteRequestDTO.getFechaEmision() != null) {
            if (recetaClienteRequestDTO.getFechaEmision().isBefore(LocalDate.now().minusDays(1))) {
                log.warn("Receta con fecha de emision anterior al dia en curso. runCliente={}, fechaEmision={}",
                        recetaClienteRequestDTO.getRunCliente(), recetaClienteRequestDTO.getFechaEmision());
                throw new ReglaNegocioException("La fecha de emision de la receta no puede ser anterior a la fecha del dia en curso.");
            }
            if (recetaClienteRequestDTO.getFechaEmision().isAfter(LocalDate.now().plusDays(1))) {
                log.warn("Receta con fecha de emision futura. runCliente={}, fechaEmision={}",
                        recetaClienteRequestDTO.getRunCliente(), recetaClienteRequestDTO.getFechaEmision());
                throw new ReglaNegocioException("La fecha de emision de la receta no puede ser una fecha futura (dias, meses o anos despues).");
            }
        }
        if (recetaClienteRequestDTO.getFechaVencimiento() != null) {
            if (recetaClienteRequestDTO.getFechaVencimiento().isBefore(LocalDate.now().minusDays(1))) {
                log.warn("Receta con fecha de vencimiento anterior a la fecha actual. runCliente={}, fechaVencimiento={}",
                        recetaClienteRequestDTO.getRunCliente(), recetaClienteRequestDTO.getFechaVencimiento());
                throw new ReglaNegocioException("La fecha de vencimiento de la receta no puede ser anterior a la fecha actual.");
            }
            
            LocalDate minVencimiento = recetaClienteRequestDTO.getFechaEmision().plusMonths(1);
            LocalDate maxVencimiento = recetaClienteRequestDTO.getFechaEmision().plusYears(1);
            
            if (recetaClienteRequestDTO.getFechaVencimiento().isBefore(minVencimiento)) {
                log.warn("Receta con fecha de vencimiento menor a un mes de emision. runCliente={}, fechaEmision={}, fechaVencimiento={}",
                        recetaClienteRequestDTO.getRunCliente(), recetaClienteRequestDTO.getFechaEmision(), recetaClienteRequestDTO.getFechaVencimiento());
                throw new ReglaNegocioException("La fecha de vencimiento debe ser de al menos un mes a partir de la fecha de emision.");
            }
            if (recetaClienteRequestDTO.getFechaVencimiento().isAfter(maxVencimiento)) {
                log.warn("Receta con fecha de vencimiento mayor a un ano de emision. runCliente={}, fechaEmision={}, fechaVencimiento={}",
                        recetaClienteRequestDTO.getRunCliente(), recetaClienteRequestDTO.getFechaEmision(), recetaClienteRequestDTO.getFechaVencimiento());
                throw new ReglaNegocioException("La fecha de vencimiento no puede superar un ano a partir de la fecha de emision.");
            }
        }


        RecetaCliente recetaGuardada = recetaClienteRepository.save(crearRecetaCliente(recetaClienteRequestDTO));
        log.info("Receta registrada exitosamente. id={}, runCliente={}, folio={}",
                recetaGuardada.getId(), recetaGuardada.getRunCliente(), recetaGuardada.getFolioReceta());
        return RecetaClienteResponseDTO.from(recetaGuardada);
    }

    public List<RecetaClienteResponseDTO> listarPorRunCliente(String runCliente) {
        // 1. Validar que el cliente exista en MSclienteBeneficio
        clienteBeneficioClient.obtenerClientePorRun(runCliente);
        
        // 2. Obtener las recetas
        List<RecetaCliente> recetas = recetaClienteRepository.findByRunClienteOrderByFechaEmisionDesc(runCliente);
        if (recetas.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron recetas medicas registradas para el cliente con RUN: " + runCliente);
        }
        
        return recetas.stream()
                .map(RecetaClienteResponseDTO::from)
                .toList();
    }

    public RecetaClienteResponseDTO obtenerPorId(Long id) {
        return RecetaClienteResponseDTO.from(buscarEntidadPorId(id));
    }

    public void desactivarReceta(Long id) {
        log.info("Solicitando desactivacion de receta. id={}", id);
        RecetaCliente recetaCliente = buscarEntidadPorId(id);
        recetaCliente.setActiva(false);
        recetaClienteRepository.save(recetaCliente);
        log.info("Receta desactivada exitosamente. id={}, runCliente={}, folio={}",
                recetaCliente.getId(), recetaCliente.getRunCliente(), recetaCliente.getFolioReceta());
    }

    private RecetaCliente buscarEntidadPorId(Long id) {
        if (id == null || id <= 0) {
            log.warn("Se solicito una receta con ID invalido. id={}", id);
            throw new RecursoNuloException("Debe ingresar un ID valido para la receta.");
        }
        return recetaClienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Receta de cliente no encontrada. id={}", id);
                    return new RecursoNoEncontradoException("Receta de cliente no encontrada.");
                });
    }

    private RecetaCliente crearRecetaCliente(RecetaClienteRequestDTO recetaClienteRequestDTO) {
        RecetaCliente recetaCliente = new RecetaCliente();
        recetaCliente.setRunCliente(recetaClienteRequestDTO.getRunCliente());
        recetaCliente.setTipoReceta(recetaClienteRequestDTO.getTipoReceta().trim());
        recetaCliente.setFolioReceta(recetaClienteRequestDTO.getFolioReceta().trim());
        recetaCliente.setFechaEmision(recetaClienteRequestDTO.getFechaEmision());
        recetaCliente.setFechaVencimiento(recetaClienteRequestDTO.getFechaVencimiento());
        recetaCliente.setActiva(true);
        return recetaCliente;
    }
}
