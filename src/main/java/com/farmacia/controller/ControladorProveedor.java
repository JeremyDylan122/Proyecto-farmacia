package com.farmacia.controller;

import com.farmacia.dto.ProveedorDto;
import com.farmacia.service.ServicioProveedor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController //le dice A spring que esto responde JSON, NO HTML.
@RequestMapping("/api/proveedores") //TODAS LAS RYUTAS PARTEN CON API/PROVEEDORES
@RequiredArgsConstructor //INYECTA SERVICIOpROVEEDOR AUTROMATICAMENTE.

public class ControladorProveedor {

    private final ServicioProveedor servicio;

    @PostMapping //CREA UN PROVEEDOR
    public ResponseEntity<ProveedorDto> crear(@Valid @RequestBody ProveedorDto dto){
        ProveedorDto creado =servicio.crearProveedor (dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping //LISTA TODOS LOS PROVEEDORES
    public ResponseEntity<List<ProveedorDto>> obtenerTodos(){
        return ResponseEntity.ok(servicio.obtenerProveedores());
    }

    @GetMapping("/{rut}") //BUSCA 1 PROVEEDOR POR RUT
    public ResponseEntity<ProveedorDto> buscarPorRut(@PathVariable String rut){
        return ResponseEntity.ok(servicio.buscarPorRut(rut));
    }

    @DeleteMapping("/{RUT}") //ELIMINA UN PROVEEDOR POR RUT
    public ResponseEntity<Void> eliminarPorRut(@PathVariable String rut){
        servicio.eliminarProveedor(rut);
        return ResponseEntity.noContent().build();
    }

}


