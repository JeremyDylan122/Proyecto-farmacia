package com.msclientebeneficio.demo.Service;

import org.springframework.stereotype.Service;

import com.msclientebeneficio.demo.Dto.ClienteDTO;
import com.msclientebeneficio.demo.Dto.ClienteDTOMapper;
import com.msclientebeneficio.demo.Exception.RecursoNoEncontradoException;
import com.msclientebeneficio.demo.Exception.RecursoYaExisteException;
import com.msclientebeneficio.demo.Model.Cliente;
import com.msclientebeneficio.demo.Repository.ClienteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteDTOMapper clienteDTOMapper;

    public ClienteDTO obtenerClientePorRun(String run) {
        log.info("Buscando cliente por RUN. run={}", run);
        Cliente cliente = clienteRepository.findByRun(run);
        if (cliente == null) {
            log.warn("Cliente no encontrado. run={}", run);
            throw new RecursoNoEncontradoException("Cliente no encontrado con RUN: " + run);
        }
        return clienteDTOMapper.toDTO(cliente);
    }

    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        if (clienteDTO == null) {
            log.warn("Se intento crear un cliente nulo.");
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        log.info("Creando cliente. run={}, idBeneficio={}", clienteDTO.getRun(), clienteDTO.getIdBeneficio());
        if (clienteRepository.existsByRun(clienteDTO.getRun())) {
            log.warn("Se intento crear un cliente duplicado. run={}", clienteDTO.getRun());
            throw new RecursoYaExisteException("Ya existe un cliente con el RUN: " + clienteDTO.getRun());
        }
        Cliente cliente = clienteDTOMapper.toModel(clienteDTO);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente creado exitosamente. run={}", clienteGuardado.getRun());
        return clienteDTOMapper.toDTO(clienteGuardado);
    }

    public boolean eliminarCliente(String run, String dv) {
        if (run == null || dv == null) {
            log.warn("Se intento eliminar un cliente con RUN o DV nulo. run={}, dv={}", run, dv);
            throw new IllegalArgumentException("El RUN y el DV no pueden ser nulos.");
        }
        log.info("Eliminando cliente. run={}, dv={}", run, dv);
        if (!clienteRepository.existsByRun(run)) {
            log.warn("Se intento eliminar un cliente inexistente. run={}, dv={}", run, dv);
            throw new RecursoNoEncontradoException("Cliente no encontrado con RUN: " + run);
        }
        clienteRepository.deleteByRunAndDv(run, dv);
        log.info("Cliente eliminado exitosamente. run={}, dv={}", run, dv);
        return true;
    }

    public ClienteDTO actualizarCliente(String run, ClienteDTO clienteDTO) {
        if (run == null || run.isBlank()) {
            log.warn("Se intento actualizar un cliente con RUN nulo o vacio.");
            throw new IllegalArgumentException("El RUN no puede ser nulo.");
        }
        if (clienteDTO == null) {
            log.warn("Se intento actualizar un cliente con DTO nulo. run={}", run);
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        log.info("Actualizando cliente. run={}, idBeneficio={}", run, clienteDTO.getIdBeneficio());
        Cliente clienteExistente = clienteRepository.findByRun(run);
        if (clienteExistente == null) {
            log.warn("Se intento actualizar un cliente inexistente. run={}", run);
            throw new RecursoNoEncontradoException("Cliente no encontrado con RUN: " + run);
        }
        clienteExistente.setCorreo(clienteDTO.getCorreo());
        clienteExistente.setTelefono(clienteDTO.getTelefono());
        if (clienteDTO.getIdBeneficio() != null) {
            clienteExistente.setBeneficio(clienteDTOMapper.toModel(clienteDTO).getBeneficio());
        }
        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        log.info("Cliente actualizado exitosamente. run={}", clienteActualizado.getRun());
        return clienteDTOMapper.toDTO(clienteActualizado);
    }
}
