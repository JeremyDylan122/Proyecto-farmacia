package com.compra.farma.controller;

import com.compra.farma.dto.DtoDetalle;
import com.compra.farma.service.ServicioDetalle;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detalles")
@RequiredArgsConstructor
@Tag(name = "Detalles", description = "Endpoints para gestionar detalles de compras y facturas")
public class ControladorDetalle {

    private final ServicioDetalle servicioDetalle;

    @Operation(
        summary = "Listar detalles por factura",
        description = "Obtiene una lista de detalles asociados a una factura específica utilizando su ID"
    )
    @ApiResponses(value = {

        @ApiResponse(responseCode = "200", description = "Lista de detalles obtenida exitosamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DtoDetalle.class)))),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada",
            content = @Content)
    })
    @GetMapping("/factura/{idFactura}")
    public ResponseEntity<List<DtoDetalle>> listarPorFactura(
        @Parameter(description = "ID de la factura a buscar") 
        @PathVariable Long idFactura
    ) {
        return ResponseEntity.ok(servicioDetalle.listarPorFactura(idFactura));
    }

    @Operation(
        summary = "Obtener detalle por ID",
        description = "Obtiene los detalles de un detalle específico utilizando su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = DtoDetalle.class))),
        @ApiResponse(responseCode = "404", description = "Detalle no encontrado",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DtoDetalle> obtenerPorId(
        @Parameter(description = "ID del detalle a buscar") 
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(servicioDetalle.obtenerPorId(id));
    }

    @Operation(
        summary = "Eliminar detalle",
        description = "Elimina un detalle específico utilizando su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Detalle eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
        @Parameter(description = "ID del detalle a eliminar") 
        @PathVariable Long id
    ) {
        servicioDetalle.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
