package com.compra.farma.controller;

import com.compra.farma.dto.DtoCompra;
import com.compra.farma.service.ServicioCompra;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/compras")
public class ControladorCompra {

    private final ServicioCompra servicioCompra;

    public ControladorCompra(ServicioCompra servicioCompra) {
        this.servicioCompra = servicioCompra;
    }
    @PostMapping
    public ResponseEntity<DtoCompra> crear(@Valid @RequestBody DtoCompra dto){
        DtoCompra compraCreada = servicioCompra.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compraCreada);
    }
    
    @GetMapping
    public ResponseEntity<List<DtoCompra>> listar(){
        return ResponseEntity.ok(servicioCompra.listaCompras());
    }

    @GetMapping("/{idOrdenCompra}")
    public ResponseEntity<DtoCompra> obtenerPorId(@PathVariable Long idOrdenCompra){
            return ResponseEntity.ok(servicioCompra.buscarPorId(idOrdenCompra));
        }

    @DeleteMapping("/{idOrdenCompra}")
    public ResponseEntity <Void> eliminarCompra(@PathVariable Long idOrdenCompra){
        servicioCompra.eliminarCompra(idOrdenCompra);
        return ResponseEntity.noContent().build();
    }

}

