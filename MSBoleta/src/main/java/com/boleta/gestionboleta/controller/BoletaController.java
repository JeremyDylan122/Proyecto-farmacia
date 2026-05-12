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

import com.boleta.gestionboleta.dto.ActualizarBoletaProductosDTO;
import com.boleta.gestionboleta.dto.BoletaDTO;
import com.boleta.gestionboleta.dto.CrearBoletaDTO;
import com.boleta.gestionboleta.service.BoletaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boletas")
@RequiredArgsConstructor
public class BoletaController {

    private final BoletaService boletaService;

    @PostMapping
    public ResponseEntity<BoletaDTO> crearBoleta(@Valid @RequestBody CrearBoletaDTO crearBoletaDTO) {
        BoletaDTO boletaDTO = boletaService.crearBoleta(crearBoletaDTO);
        return ResponseEntity.status(201).body(boletaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{run}")
    public ResponseEntity<List<BoletaDTO>> listarPorRunCliente(@PathVariable String run) {
        return ResponseEntity.ok(boletaService.listarPorRunCliente(run));
    }

    @GetMapping("/producto/{sku}")
    public ResponseEntity<List<BoletaDTO>> listarPorSkuProducto(@PathVariable Long sku) {
        return ResponseEntity.ok(boletaService.listarPorSkuProducto(sku));
    }

    @PutMapping("/{id}/productos")
    public ResponseEntity<BoletaDTO> actualizarProductos(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarBoletaProductosDTO actualizarBoletaProductosDTO) {
        return ResponseEntity.ok(boletaService.actualizarProductos(id, actualizarBoletaProductosDTO));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<Void> anularBoleta(@PathVariable Long id) {
        boletaService.anularBoleta(id);
        return ResponseEntity.noContent().build();
    }
}
