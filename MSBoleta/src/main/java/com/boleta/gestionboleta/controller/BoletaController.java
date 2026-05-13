package com.boleta.gestionboleta.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boleta.gestionboleta.dto.ActualizarBoletaProductosRequestDTO;
import com.boleta.gestionboleta.dto.BoletaResponseDTO;
import com.boleta.gestionboleta.dto.CrearBoletaRequestDTO;
import com.boleta.gestionboleta.service.BoletaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boletas")
@RequiredArgsConstructor
public class BoletaController {

    private final BoletaService boletaService;

    @PostMapping
    public ResponseEntity<BoletaResponseDTO> crearBoleta(@Valid @RequestBody CrearBoletaRequestDTO crearBoletaRequestDTO) {
        BoletaResponseDTO boletaResponseDTO = boletaService.crearBoleta(crearBoletaRequestDTO);
        return ResponseEntity.status(201).body(boletaResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{run}")
    public ResponseEntity<List<BoletaResponseDTO>> listarPorRunCliente(@PathVariable String run) {
        return ResponseEntity.ok(boletaService.listarPorRunCliente(run));
    }

    @GetMapping("/producto/{sku}")
    public ResponseEntity<List<BoletaResponseDTO>> listarPorSkuProducto(@PathVariable Long sku) {
        return ResponseEntity.ok(boletaService.listarPorSkuProducto(sku));
    }

    @PutMapping("/{id}/productos")
    public ResponseEntity<BoletaResponseDTO> actualizarProductos(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarBoletaProductosRequestDTO actualizarBoletaProductosRequestDTO) {
        return ResponseEntity.ok(boletaService.actualizarProductos(id, actualizarBoletaProductosRequestDTO));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<Void> anularBoleta(@PathVariable Long id) {
        boletaService.anularBoleta(id);
        return ResponseEntity.noContent().build();
    }
}
