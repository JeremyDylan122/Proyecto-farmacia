package com.inventario.msinventario.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.msinventario.dto.loteDTOs.LoteDTO;
import com.inventario.msinventario.service.LoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Lotes",
    description =  "Operaciones de gestion de lotes"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lotes")
public class LoteController {

    private final LoteService loteService;

    @Operation(summary="Listar lotes", description="Obtiene todos los lotes registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de lotes obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<LoteDTO>> listarTodos() {
        List<LoteDTO> lotes = loteService.listarTodos();
        return ResponseEntity.ok(lotes);
    }

    @Operation(summary="Listar un lote", description="Obtiene un lote registrado mediante ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lote obtenido exitosamente."),
            @ApiResponse(responseCode = "404", description = "Lote no encontrado.")
    })
    @GetMapping("/{codigoLote}")
    public ResponseEntity<LoteDTO> buscarPorId(@PathVariable String codigoLote) {
        LoteDTO lote = loteService.buscarPorId(codigoLote);
        return ResponseEntity.ok(lote);
    }

}
