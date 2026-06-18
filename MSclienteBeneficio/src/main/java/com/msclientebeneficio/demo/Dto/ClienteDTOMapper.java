package com.msclientebeneficio.demo.Dto;

import org.springframework.stereotype.Component;

import com.msclientebeneficio.demo.Model.Cliente;
import com.msclientebeneficio.demo.Service.BeneficioService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClienteDTOMapper {
    private final BeneficioService beneficioService;

    public ClienteDTO toDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        ClienteDTO ClieDTO = new ClienteDTO();
        ClieDTO.setRun(cliente.getRun());
        ClieDTO.setDv(cliente.getDv());
        ClieDTO.setNombre(cliente.getNombre());
        ClieDTO.setApellido(cliente.getApellido());
        ClieDTO.setCorreo(cliente.getCorreo());
        ClieDTO.setTelefono(cliente.getTelefono());
        if (cliente.getBeneficio() != null) {
            ClieDTO.setIdBeneficio(cliente.getBeneficio().getId());
        }
        return ClieDTO;
    }

    public Cliente toModel(ClienteDTO clienteDTO) {
        if (clienteDTO == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setRun(clienteDTO.getRun());
        cliente.setDv(clienteDTO.getDv());
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setCorreo(clienteDTO.getCorreo());
        cliente.setTelefono(clienteDTO.getTelefono());
        if (clienteDTO.getIdBeneficio() != null) {
            cliente.setBeneficio(beneficioService.findById(clienteDTO.getIdBeneficio()));
        }
        return cliente;
    }

}