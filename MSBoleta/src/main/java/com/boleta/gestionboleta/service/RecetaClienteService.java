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

    public RecetaClienteResponseDTO registrarReceta(RecetaClienteRequestDTO recetaClienteRequestDTO) {
        if (recetaClienteRequestDTO == null) {
            log.warn("Se intento registrar una receta con request nulo.");
            throw new RecursoNuloException("La receta del cliente no puede ser nula.");
        }
        log.info("Registrando receta para cliente. runCliente={}, tipoReceta={}, folio={}",
                recetaClienteRequestDTO.getRunCliente(),
                recetaClienteRequestDTO.getTipoReceta(),
                recetaClienteRequestDTO.getFolioReceta());
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
        if (recetaClienteRequestDTO.getFechaVencimiento() != null
                && recetaClienteRequestDTO.getFechaVencimiento().isBefore(recetaClienteRequestDTO.getFechaEmision())) {
            log.warn("Receta con fechas invalidas. runCliente={}, folio={}, fechaEmision={}, fechaVencimiento={}",
                    recetaClienteRequestDTO.getRunCliente(),
                    recetaClienteRequestDTO.getFolioReceta(),
                    recetaClienteRequestDTO.getFechaEmision(),
                    recetaClienteRequestDTO.getFechaVencimiento());
            throw new ReglaNegocioException("La fecha de vencimiento no puede ser anterior a la fecha de emision.");
        }
        if (recetaClienteRequestDTO.getFechaEmision() != null
                && recetaClienteRequestDTO.getFechaEmision().isAfter(LocalDate.now())) {
            log.warn("Receta con fecha de emision futura. runCliente={}, folio={}, fechaEmision={}",
                    recetaClienteRequestDTO.getRunCliente(),
                    recetaClienteRequestDTO.getFolioReceta(),
                    recetaClienteRequestDTO.getFechaEmision());
            throw new ReglaNegocioException("La fecha de emision de la receta no puede ser una fecha futura (dias, meses o años despues).");
        }

        RecetaCliente recetaGuardada = recetaClienteRepository.save(crearRecetaCliente(recetaClienteRequestDTO));
        log.info("Receta registrada exitosamente. id={}, runCliente={}, folio={}",
                recetaGuardada.getId(), recetaGuardada.getRunCliente(), recetaGuardada.getFolioReceta());
        return RecetaClienteResponseDTO.from(recetaGuardada);
    }

    public List<RecetaClienteResponseDTO> listarPorRunCliente(String runCliente) {
        return recetaClienteRepository.findByRunClienteOrderByFechaEmisionDesc(runCliente)
                .stream()
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
