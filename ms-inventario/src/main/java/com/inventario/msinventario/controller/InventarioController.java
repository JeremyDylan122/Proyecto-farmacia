package com.inventario.msinventario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.msinventario.dto.compraDTOs.CompraRequestDTO;
import com.inventario.msinventario.dto.compraDTOs.CompraResponseDTO;
import com.inventario.msinventario.dto.ventaDTOs.VentaRequestDTO;
import com.inventario.msinventario.dto.ventaDTOs.VentaResponseDTO;
import com.inventario.msinventario.service.InventarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Inventario", description = "Operaciones de entradas y salidas")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    @Operation(summary = "Registrar compra", description = "Entrada de lotes, registro de movimiento y actualizacion de stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compra registrada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud invalidos."),
            @ApiResponse(responseCode = "503", description = "Error en comunicacion con micro servicio.")
    })
    @PostMapping("/compras")
    public ResponseEntity<CompraResponseDTO> registrarEntrada(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos requeridos para registrar compra.") @Valid @RequestBody CompraRequestDTO request) {
        CompraResponseDTO response = inventarioService.registrarCompra(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Realizar venta", description = "Salida de productos con validacion FEFO y registro de movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta realizada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud invalidos."),
            @ApiResponse(responseCode = "503", description = "Error en comunicacion con micro servicio.")
    })
    @PostMapping("/ventas")
    public ResponseEntity<VentaResponseDTO> validarVenta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos requeridos para registrar venta.") @Valid @RequestBody VentaRequestDTO request) {
        VentaResponseDTO response = inventarioService.procesarVenta(request);
        return ResponseEntity.ok(response);
    }
}
