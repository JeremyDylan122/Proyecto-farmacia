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

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteDTOMapper clienteDTOMapper;

    public ClienteDTO obtenerClientePorRun(String run) {
        Cliente cliente = clienteRepository.findByRun(run);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con RUN: " + run);
        }
        return clienteDTOMapper.toDTO(cliente);
    }

    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        if (clienteDTO == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        if (clienteRepository.existsByRun(clienteDTO.getRun())) {
            throw new RecursoYaExisteException("Ya existe un cliente con el RUN: " + clienteDTO.getRun());
        }
        Cliente cliente = clienteDTOMapper.toModel(clienteDTO);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return clienteDTOMapper.toDTO(clienteGuardado);
    }
    public boolean eliminarCliente(String run, String dv) {
        if (run == null || dv == null) {
            throw new IllegalArgumentException("El RUN y el DV no pueden ser nulos.");
        }
        if (!clienteRepository.existsByRun(run)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con RUN: " + run);
        }
        clienteRepository.deleteByRun(run, dv);
        return true;
    }
    public ClienteDTO actualizarCliente(String run, ClienteDTO clienteDTO) {
        if (run == null) {
            throw new IllegalArgumentException("El RUN no puede ser nulo.");
        }
        Cliente clienteExistente = clienteRepository.findByRun(run);
        if (clienteExistente == null) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con RUN: " + run);
        }
        clienteExistente.setCorreo(clienteDTO.getCorreo());
        clienteExistente.setTelefono(clienteDTO.getTelefono());
        if (clienteDTO.getIdBeneficio() != null) {
            clienteExistente.setBeneficio(clienteDTOMapper.toModel(clienteDTO).getBeneficio());
        }
        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        return clienteDTOMapper.toDTO(clienteActualizado);
    }

}
