package com.boleta.gestionboleta.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boleta.gestionboleta.dto.RecetaClienteRequestDTO;
import com.boleta.gestionboleta.dto.RecetaClienteResponseDTO;
import com.boleta.gestionboleta.service.RecetaClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recetas-clientes")
@RequiredArgsConstructor
@Tag(name = "Recetas de Clientes", description = "Controlador para gestionar las recetas médicas de los clientes")
public class RecetaClienteController {

    private final RecetaClienteService recetaClienteService;

    @PostMapping
    @Operation(summary = "Registrar receta médica", description = "Registra una nueva receta médica para un cliente, validando que no sea de venta libre ni tenga fechas de emisión futuras.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Receta registrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o regla de negocio no cumplida"),
        @ApiResponse(responseCode = "409", description = "Receta duplicada detectada para este cliente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<RecetaClienteResponseDTO> registrarReceta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la receta médica a registrar")
            @Valid @RequestBody RecetaClienteRequestDTO recetaClienteRequestDTO
    ) {
        RecetaClienteResponseDTO recetaClienteResponseDTO = recetaClienteService.registrarReceta(recetaClienteRequestDTO);
        return ResponseEntity.status(201).body(recetaClienteResponseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar receta por ID", description = "Obtiene los detalles de una receta médica específica por su identificador único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Receta encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<RecetaClienteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recetaClienteService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{run}")
    @Operation(summary = "Listar recetas por RUN de cliente", description = "Obtiene un listado de todas las recetas médicas asociadas a un cliente por su RUN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<RecetaClienteResponseDTO>> listarPorRunCliente(@PathVariable String run) {
        return ResponseEntity.ok(recetaClienteService.listarPorRunCliente(run));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar receta médica", description = "Marca una receta médica específica como inactiva por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Receta desactivada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> desactivarReceta(@PathVariable Long id) {
        recetaClienteService.desactivarReceta(id);
        return ResponseEntity.noContent().build();
    }
}
