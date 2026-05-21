package com.msclientebeneficio.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msclientebeneficio.demo.Dto.ClienteDTO;
import com.msclientebeneficio.demo.Service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/{run}")
    public ResponseEntity<ClienteDTO> obtenerClientePorRun(@PathVariable String run) {
        return ResponseEntity.ok(clienteService.obtenerClientePorRun(run));
    }

    @PostMapping()
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteCreado = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.status(201).body(clienteCreado);
    }

    @PutMapping("/{run}")
    public ResponseEntity<ClienteDTO> actualizarCliente(@Valid @RequestBody ClienteDTO clienteDTO, @PathVariable String run) {
        return ResponseEntity.ok(clienteService.actualizarCliente(run, clienteDTO));
    }

    @DeleteMapping("/{run}/{dv}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String run, @PathVariable String dv) {
        clienteService.eliminarCliente(run, dv);
        return ResponseEntity.noContent().build();
    }

}
