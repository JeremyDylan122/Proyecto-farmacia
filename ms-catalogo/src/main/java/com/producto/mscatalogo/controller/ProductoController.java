package com.producto.mscatalogo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.producto.mscatalogo.dto.ProductoDTO;
import com.producto.mscatalogo.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("")
    public ResponseEntity<List<ProductoDTO>> obtenerTodos() {
        List<ProductoDTO> productos = productoService.listarTodos();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/{sku}")
    public ResponseEntity<ProductoDTO> obtenerPorSku(@PathVariable Long sku) {
        ProductoDTO productoDTO = productoService.buscarPorSku(sku);
        return ResponseEntity.ok(productoDTO);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoDTO>> obtenerPorCategoria(@PathVariable String categoria) {
        List<ProductoDTO> productos = productoService.listarPorCategoria(categoria);          
        return ResponseEntity.ok(productos);           
    }

    @PostMapping("")
    public ResponseEntity<ProductoDTO> agregarProducto(@Valid @RequestBody ProductoDTO productoDTO) { 
        ProductoDTO nuevoProducto = productoService.agregarProducto(productoDTO);       
        return ResponseEntity.status(201).body(nuevoProducto);
    }

    @PutMapping("/{sku}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long sku,@Valid @RequestBody ProductoDTO productoDTO) {
        ProductoDTO actualizado = productoService.actualizarProducto(sku, productoDTO);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> ocultarProducto(@PathVariable Long sku) {
        productoService.ocultarProducto(sku);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{sku}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long sku) {
        productoService.activarProducto(sku);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{sku}/visible")
    public ResponseEntity<Void> cambiarVisibilidad(@PathVariable Long sku, @RequestParam boolean activo) {   
        productoService.cambiarEstadoVisibilidad(sku, activo);
        return ResponseEntity.ok().build(); 
    }
    
}
