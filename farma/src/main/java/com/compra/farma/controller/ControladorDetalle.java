package com.compra.farma.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compra.farma.dto.DtoDetalle;
import com.compra.farma.service.ServicioDetalle;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/detalles")
@RequiredArgsConstructor
public class ControladorDetalle {

    private final ServicioDetalle servicioDetalle;

    @GetMapping("/factura/{idFactura}")
    public ResponseEntity<List<DtoDetalle>> listarPorFactura (@PathVariable Long idFactura) {
        return ResponseEntity.ok(servicioDetalle.listarPorFactura(idFactura));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DtoDetalle> obtenerPorId (@PathVariable Long id) {
        return ResponseEntity.ok(servicioDetalle.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar (@PathVariable Long id) {
        servicioDetalle.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
