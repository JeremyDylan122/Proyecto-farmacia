package com.compra.farma.controller;

import com.compra.farma.dto.DtoFactura;
import com.compra.farma.service.ServicioFactura;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class ControladorFactura {

    private final ServicioFactura servicioFactura;

    @PostMapping
    public ResponseEntity<DtoFactura> crearFactura(@RequestBody DtoFactura dto) {
        return ResponseEntity.ok(servicioFactura.crearFactura(dto));
        
    }
    @GetMapping("/{id}")
    public ResponseEntity<DtoFactura> buscarFacturaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicioFactura.buscarFacturaPorId(id));
    }
    @GetMapping
    public ResponseEntity<List<DtoFactura>> listarFacturas() {
        return ResponseEntity.ok(servicioFactura.listarFacturas());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        servicioFactura.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }
    
}
