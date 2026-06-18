package com.inventario.msinventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.msinventario.dto.stockDTOs.StockDTO;
import com.inventario.msinventario.service.StockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Stock",
    description =  "Operaciones para listar stock"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock")
public class StockController {

    private final StockService stockService;

    @Operation(summary="Listar stock", description="Obtiene todos el stock registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de stock obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<StockDTO>> listarStock() {
        List<StockDTO> inventarioGlobal = stockService.listarStock();
        return ResponseEntity.ok(inventarioGlobal);
    }

    @Operation(summary="Listar stock", description="Obtiene stock registrado mediante SKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock obtenido exitosamente."),
            @ApiResponse(responseCode = "404", description = "Stock no encontrado.")
    })
    @GetMapping("/{sku}")
    public ResponseEntity<StockDTO> buscarPorSku(@PathVariable Long sku) {
        StockDTO stockProducto = stockService.buscarPorSku(sku);
        return ResponseEntity.ok(stockProducto);
    }

}
