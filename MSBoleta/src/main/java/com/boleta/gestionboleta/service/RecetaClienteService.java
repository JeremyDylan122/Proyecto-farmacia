package com.boleta.gestionboleta.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boleta.gestionboleta.dto.RecetaClienteRequestDTO;
import com.boleta.gestionboleta.dto.RecetaClienteResponseDTO;
import com.boleta.gestionboleta.dto.RecetaClienteResponseDTOMapper;
import com.boleta.gestionboleta.excepcions.RecursoDuplicadoException;
import com.boleta.gestionboleta.excepcions.RecursoNoEncontradoException;
import com.boleta.gestionboleta.excepcions.RecursoNuloException;
import com.boleta.gestionboleta.excepcions.ReglaNegocioException;
import com.boleta.gestionboleta.model.RecetaCliente;
import com.boleta.gestionboleta.repository.RecetaClienteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RecetaClienteService {

    private final RecetaClienteRepository recetaClienteRepository;
    private final RecetaClienteResponseDTOMapper recetaClienteResponseDTOMapper;

    public RecetaClienteResponseDTO registrarReceta(RecetaClienteRequestDTO recetaClienteRequestDTO) {
        if (recetaClienteRequestDTO == null) {
            throw new RecursoNuloException("La receta del cliente no puede ser nula.");
        }
        if (recetaClienteRepository.existsByRunClienteAndFolioReceta(
                recetaClienteRequestDTO.getRunCliente(),
                recetaClienteRequestDTO.getFolioReceta())) {
            throw new RecursoDuplicadoException("Ya existe una receta registrada con ese folio para el cliente.");
        }
        if ("venta libre".equalsIgnoreCase(recetaClienteRequestDTO.getTipoReceta().trim())) {
            throw new ReglaNegocioException("No es necesario registrar recetas para productos de venta libre.");
        }
        if (recetaClienteRequestDTO.getFechaVencimiento() != null
                && recetaClienteRequestDTO.getFechaVencimiento().isBefore(recetaClienteRequestDTO.getFechaEmision())) {
            throw new ReglaNegocioException("La fecha de vencimiento no puede ser anterior a la fecha de emision.");
        }

        RecetaCliente recetaCliente = new RecetaCliente();
        recetaCliente.setRunCliente(recetaClienteRequestDTO.getRunCliente());
        recetaCliente.setTipoReceta(recetaClienteRequestDTO.getTipoReceta().trim());
        recetaCliente.setFolioReceta(recetaClienteRequestDTO.getFolioReceta().trim());
        recetaCliente.setFechaEmision(recetaClienteRequestDTO.getFechaEmision());
        recetaCliente.setFechaVencimiento(recetaClienteRequestDTO.getFechaVencimiento());
        recetaCliente.setActiva(true);

        RecetaCliente recetaGuardada = recetaClienteRepository.save(recetaCliente);
        return recetaClienteResponseDTOMapper.toDTO(recetaGuardada);
    }

    public List<RecetaClienteResponseDTO> listarPorRunCliente(String runCliente) {
        return recetaClienteRepository.findByRunClienteOrderByFechaEmisionDesc(runCliente)
                .stream()
                .map(recetaClienteResponseDTOMapper::toDTO)
                .toList();
    }

    public RecetaClienteResponseDTO obtenerPorId(Long id) {
        return recetaClienteResponseDTOMapper.toDTO(buscarEntidadPorId(id));
    }

    public void desactivarReceta(Long id) {
        RecetaCliente recetaCliente = buscarEntidadPorId(id);
        recetaCliente.setActiva(false);
        recetaClienteRepository.save(recetaCliente);
    }

    private RecetaCliente buscarEntidadPorId(Long id) {
        if (id == null || id <= 0) {
            throw new RecursoNuloException("Debe ingresar un ID valido para la receta.");
        }
        return recetaClienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta de cliente no encontrada."));
    }
}
