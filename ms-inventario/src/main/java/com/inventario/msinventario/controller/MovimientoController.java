package com.inventario.msinventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.msinventario.dto.movimientoDTOs.MovimientoDTO;
import com.inventario.msinventario.service.MovimientoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Movimientos",
    description =  "Operaciones para listar movimientos"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @Operation(summary="Listar movimientos", description="Obtiene todos los movimientos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de Movimientos obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> listarTodos() {
        List<MovimientoDTO> movimientos = movimientoService.listarTodos();
        return ResponseEntity.ok(movimientos);
    }

    @Operation(summary="Listar un movimiento", description="Obtiene un movimiento registrado mediante ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento obtenido exitosamente."),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado.")
    })
    @GetMapping("/{idMovimiento}")
    public ResponseEntity<MovimientoDTO> listarPorId(@PathVariable Long idMovimiento) {
        MovimientoDTO movimiento = movimientoService.listarPorId(idMovimiento);
        return ResponseEntity.ok(movimiento);
    }

}
