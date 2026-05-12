package com.boleta.gestionboleta.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boleta.gestionboleta.dto.RecetaClienteDTO;
import com.boleta.gestionboleta.dto.RecetaClienteRequestDTO;
import com.boleta.gestionboleta.service.RecetaClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recetas-clientes")
@RequiredArgsConstructor
public class RecetaClienteController {

    private final RecetaClienteService recetaClienteService;

    @PostMapping
    public ResponseEntity<RecetaClienteDTO> registrarReceta(@Valid @RequestBody RecetaClienteRequestDTO recetaClienteRequestDTO) {
        RecetaClienteDTO recetaClienteDTO = recetaClienteService.registrarReceta(recetaClienteRequestDTO);
        return ResponseEntity.status(201).body(recetaClienteDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaClienteDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recetaClienteService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{run}")
    public ResponseEntity<List<RecetaClienteDTO>> listarPorRunCliente(@PathVariable String run) {
        return ResponseEntity.ok(recetaClienteService.listarPorRunCliente(run));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarReceta(@PathVariable Long id) {
        recetaClienteService.desactivarReceta(id);
        return ResponseEntity.noContent().build();
    }
}
