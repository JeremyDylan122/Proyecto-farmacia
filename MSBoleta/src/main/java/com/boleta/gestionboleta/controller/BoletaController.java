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

import com.boleta.gestionboleta.dto.ActualizarBoletaProductosRequestDTO;
import com.boleta.gestionboleta.dto.BoletaResponseDTO;
import com.boleta.gestionboleta.dto.CrearBoletaRequestDTO;
import com.boleta.gestionboleta.service.BoletaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boletas")
@RequiredArgsConstructor
@Tag(name = "Boletas", description = "Controlador para gestionar la facturación y boletas de venta")
public class BoletaController {

    private final BoletaService boletaService;

    @PostMapping
    @Operation(summary = "Crear nueva boleta", description = "Genera una nueva boleta de venta para un cliente, consolidando productos y aplicando descuentos y validaciones.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Boleta creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o regla de negocio no cumplida"),
        @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BoletaResponseDTO> crearBoleta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la boleta a crear")
            @Valid @RequestBody CrearBoletaRequestDTO crearBoletaRequestDTO
    ) {
        BoletaResponseDTO boletaResponseDTO = boletaService.crearBoleta(crearBoletaRequestDTO);
        return ResponseEntity.status(201).body(boletaResponseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar boleta por ID", description = "Recupera los detalles completos de una boleta de venta específica utilizando su identificador único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Boleta encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Boleta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BoletaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{run}")
    @Operation(summary = "Listar boletas por RUN de cliente", description = "Obtiene un listado de todas las boletas asociadas al RUN de un cliente específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<BoletaResponseDTO>> listarPorRunCliente(@PathVariable String run) {
        return ResponseEntity.ok(boletaService.listarPorRunCliente(run));
    }

    @GetMapping("/producto/{sku}")
    @Operation(summary = "Listar boletas por SKU de producto", description = "Recupera todas las boletas que contienen al menos una unidad del producto especificado por su SKU.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<BoletaResponseDTO>> listarPorSkuProducto(@PathVariable Long sku) {
        return ResponseEntity.ok(boletaService.listarPorSkuProducto(sku));
    }

    @PutMapping("/{id}/productos")
    @Operation(summary = "Actualizar productos de una boleta", description = "Permite modificar el listado de productos de una boleta existente y recalcula los montos totales.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Productos actualizados y boleta recalculada con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Boleta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BoletaResponseDTO> actualizarProductos(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevo listado de productos para la boleta")
            @Valid @RequestBody ActualizarBoletaProductosRequestDTO actualizarBoletaProductosRequestDTO
    ) {
        return ResponseEntity.ok(boletaService.actualizarProductos(id, actualizarBoletaProductosRequestDTO));
    }

    @PatchMapping("/{id}/anular")
    @Operation(summary = "Anular boleta de venta", description = "Marca una boleta existente como anulada y registra la fecha de anulación.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Boleta anulada con éxito"),
        @ApiResponse(responseCode = "400", description = "La boleta ya se encuentra anulada"),
        @ApiResponse(responseCode = "404", description = "Boleta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> anularBoleta(@PathVariable Long id) {
        boletaService.anularBoleta(id);
        return ResponseEntity.noContent().build();
    }
}
