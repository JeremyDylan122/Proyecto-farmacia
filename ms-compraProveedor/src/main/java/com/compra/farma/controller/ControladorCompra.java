package com.compra.farma.controller;

import com.compra.farma.dto.DtoCompra;
import com.compra.farma.service.ServicioCompra;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor 
@Tag(name = "Compras", description = "Endpoints para gestionar compras a proveedores")
public class ControladorCompra {

    private final ServicioCompra servicioCompra;

    @Operation(
        summary = "Crear una nueva compra", 
        description = "Crea una nueva compra a un proveedor"
    )   
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Compra creada exitosamente",
            content = @Content(schema = @Schema(implementation = DtoCompra.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida",
            content = @Content)
    })
    @PostMapping
    public ResponseEntity<DtoCompra> crear(@Valid @RequestBody DtoCompra dto) {
        DtoCompra compraCreada = servicioCompra.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compraCreada);
    }

    @Operation(
        summary = "Listar todas las compras",
        description = "Obtiene una lista de todas las compras realizadas a proveedores"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de compras obtenida exitosamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DtoCompra.class))))
    })
    @GetMapping
    public ResponseEntity<List<DtoCompra>> listar() {
        return ResponseEntity.ok(servicioCompra.listaCompras());
    }

    @Operation(
        summary = "Obtener compra por ID",
        description = "Obtiene los detalles de una compra específica utilizando su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compra obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = DtoCompra.class))),
        @ApiResponse(responseCode = "404", description = "Compra no encontrada",
            content = @Content)
    })
    @GetMapping("/{idOrdenCompra}")
    public ResponseEntity<DtoCompra> obtenerPorId(
        @Parameter(description = "ID de la orden de compra a buscar", schema = @Schema(type = "string")) 
        @PathVariable String idOrdenCompra
    ) {
        return ResponseEntity.ok(servicioCompra.buscarPorId(idOrdenCompra));
    }

    @Operation(
        summary = "Eliminar una compra.",
        description = "Elimina una compra específica utilizando su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Compra eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Compra no encontrada")
    })
    @DeleteMapping("/{idOrdenCompra}")
    public ResponseEntity<Void> eliminarCompra(
        @Parameter(description = "ID de la orden de compra a eliminar", schema = @Schema(type = "string")) 
        @PathVariable String idOrdenCompra
    ) {
        servicioCompra.eliminarCompra(idOrdenCompra);
        return ResponseEntity.noContent().build();
    }
}
