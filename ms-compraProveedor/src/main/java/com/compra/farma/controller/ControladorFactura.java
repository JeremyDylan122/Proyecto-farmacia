package com.compra.farma.controller;

import com.compra.farma.dto.DtoFactura;
import com.compra.farma.service.ServicioFactura;
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
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@Tag(name = "Facturas", description = "Endpoints para gestionar facturas de compras a proveedores")
public class ControladorFactura {

    private final ServicioFactura servicioFactura;

    @Operation(
        summary = "Crear una nueva factura",
        description = "Crea una nueva factura con los datos proporcionados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Factura creada exitosamente",
            content = @Content(schema = @Schema(implementation = DtoFactura.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida",
            content = @Content)
    })
    @PostMapping
    public ResponseEntity<DtoFactura> crearFactura(@RequestBody DtoFactura dto) {
        DtoFactura facturaCreada = servicioFactura.crearFactura(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaCreada);
    }

    @Operation(
        summary = "Buscar factura por ID",
        description = "Obtiene los datos de una factura específica utilizando su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Factura encontrada",
            content = @Content(schema = @Schema(implementation = DtoFactura.class))),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DtoFactura> buscarFacturaPorId(
        @Parameter(description = "ID de la factura a buscar") 
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(servicioFactura.buscarFacturaPorId(id));
    }

    @Operation(
        summary = "Listar facturas",
        description = "Obtiene una lista de todas las facturas"
    )
    @ApiResponses(value = {

        @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida exitosamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DtoFactura.class))))
    })
    @GetMapping
    public ResponseEntity<List<DtoFactura>> listarFacturas() {
        return ResponseEntity.ok(servicioFactura.listarFacturas());
    }

    @Operation(
        summary = "Eliminar factura",
        description = "Elimina una factura específica utilizando su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Factura estructura eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(
        @Parameter(description = "ID de la factura a eliminar") 
        @PathVariable Long id
    ) {
        servicioFactura.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }
}
