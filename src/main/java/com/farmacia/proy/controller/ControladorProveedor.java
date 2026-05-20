package com.farmacia.proy.controller;

import com.farmacia.proy.dto.ProveedorRequestDto;
import com.farmacia.proy.dto.ProveedorResponseDto;
import com.farmacia.proy.service.ServicioProveedor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/proveedores") 
@RequiredArgsConstructor

public class ControladorProveedor {

    private final ServicioProveedor servicio;

    @PostMapping
    public ResponseEntity<ProveedorResponseDto> crear(@Valid @RequestBody ProveedorRequestDto dto){
        ProveedorResponseDto creado =servicio.crearProveedor (dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<ProveedorResponseDto>> obtenerTodos(){
        return ResponseEntity.ok(servicio.obtenerProveedores());
    }

    @GetMapping("/{rut}") 
    public ResponseEntity<ProveedorResponseDto> buscarPorRut(@PathVariable String rut){
        return ResponseEntity.ok(servicio.buscarPorRut(rut));
    }

    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> eliminarPorRut(@PathVariable String rut){
        servicio.eliminarProveedor(rut);
        return ResponseEntity.noContent().build();
    }

}


