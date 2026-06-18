package com.inventario.msinventario.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.msinventario.dto.movimientoDTOs.MovimientoDTO;
import com.inventario.msinventario.dto.movimientoDTOs.MovimientoDTOMapper;
import com.inventario.msinventario.exceptions.RecursoNuloException;
import com.inventario.msinventario.model.Movimiento;
import com.inventario.msinventario.repository.MovimientoRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoDTOMapper movimientoDTOMapper;

    @Transactional
    public MovimientoDTO registrarMovimiento(MovimientoDTO movimientoDTO) {
        if (movimientoDTO == null) {
            throw new RuntimeException("Error: Los datos del movimiento no pueden ser nulos.");
        }
        log.info("Registrando de forma automática movimiento de tipo [{}] para el lote: {}", 
                 movimientoDTO.getTipo(), movimientoDTO.getCodigoLote());

        Movimiento nuevoMovimiento = movimientoDTOMapper.toModel(movimientoDTO);
        Movimiento movimientoGuardado = movimientoRepository.save(nuevoMovimiento);

        log.info("Movimiento histórico guardado con éxito. ID Asignado: {}", movimientoGuardado.getIdMovimiento());

        return movimientoDTOMapper.toDTO(movimientoGuardado);
    }

    // LISTAR TODOS LOS MOVIMIENTOS
    @Transactional(readOnly = true)
    public List<MovimientoDTO> listarTodos() {
        log.info("Solicitando el historial completo de movimientos de inventario.");

        List<Movimiento> movimientos = movimientoRepository.findAll();

        return movimientos.stream()
                .map(movimientoDTOMapper::toDTO)
                .toList();
    }

    // LISTAR MOVIMIENTOS POR ID
    @Transactional(readOnly = true)
    public MovimientoDTO listarPorId(Long idMovimiento) {
        if (idMovimiento == null) {
            throw new RecursoNuloException("Error: El ID del movimiento es obligatorio para la búsqueda.");
        }

        log.info("Buscando movimiento de inventario con ID: {}", idMovimiento);

        Movimiento movimiento = movimientoRepository.findById(idMovimiento)
                .orElseThrow(() -> new RuntimeException("Error: No se encontró ningún movimiento con el ID: " + idMovimiento));

        return movimientoDTOMapper.toDTO(movimiento);
    }

}
