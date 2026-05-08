package com.msboleta.service;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.msboleta.dto.ClienteRecetaRequest;
import com.msboleta.dto.ClienteRecetaResponse;
import com.msboleta.model.ClienteReceta;
import com.msboleta.repository.ClienteRecetaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteRecetaService {

    private final ClienteRecetaRepository clienteRecetaRepository;

    public ClienteRecetaResponse asociarReceta(String runCliente, ClienteRecetaRequest request) {
        ClienteReceta clienteReceta = new ClienteReceta();
        clienteReceta.setRunCliente(runCliente);
        clienteReceta.setTipoReceta(request.getTipoReceta().trim());
        clienteReceta.setFechaAsignacion(LocalDateTime.now());
        ClienteReceta guardado = clienteRecetaRepository.save(clienteReceta);
        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ClienteRecetaResponse> listarPorRun(String runCliente) {
        return clienteRecetaRepository.findByRunCliente(runCliente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public boolean clienteTieneReceta(String runCliente, String tipoReceta) {
        return clienteRecetaRepository.existsByRunClienteAndTipoRecetaIgnoreCase(runCliente, tipoReceta);
    }

    private ClienteRecetaResponse toResponse(ClienteReceta entity) {
        return ClienteRecetaResponse.builder()
                .id(entity.getId())
                .runCliente(entity.getRunCliente())
                .tipoReceta(entity.getTipoReceta())
                .fechaAsignacion(entity.getFechaAsignacion())
                .build();
    }
}
