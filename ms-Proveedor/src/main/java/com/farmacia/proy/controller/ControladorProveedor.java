package com.farmacia.proy.controller;

import com.farmacia.proy.dto.ProveedorRequestDto;
import com.farmacia.proy.dto.ProveedorResponseDto;
import com.farmacia.proy.service.ServicioProveedor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proveedores") 
@RequiredArgsConstructor
@Tag(name = "Proveedor Controller", description = "API para la gestión de proveedores de la farmacia")
public class ControladorProveedor {

    private final ServicioProveedor servicio;

    @PostMapping
    @Operation(summary = "Crear un nuevo proveedor", description = "Registra un proveedor en el sistema a partir de los datos proporcionados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ProveedorResponseDto> crear(@Valid @RequestBody ProveedorRequestDto dto){
        ProveedorResponseDto creado = servicio.crearProveedor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los proveedores", description = "Retorna una lista completa con todos los proveedores registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida correctamente")
    public ResponseEntity<List<ProveedorResponseDto>> obtenerTodos(){
        return ResponseEntity.ok(servicio.obtenerProveedores());
    }

    @GetMapping("/{rut}") 
    @Operation(summary = "Buscar proveedor por RUT", description = "Busca y retorna los detalles de un proveedor específico usando su RUT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proveedor encontrado"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    public ResponseEntity<ProveedorResponseDto> buscarPorRut(
            @Parameter(description = "RUT del proveedor a buscar (ej: 12345678-9)", required = true) 
            @PathVariable String rut){
        return ResponseEntity.ok(servicio.buscarPorRut(rut));
    }

    @DeleteMapping("/{rut}")
    @Operation(summary = "Eliminar un proveedor", description = "Elimina de forma permanente un proveedor del sistema mediante su RUT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "24", description = "Proveedor eliminado exitosamente (No Content)"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    public ResponseEntity<Void> eliminarPorRut(
            @Parameter(description = "RUT del proveedor a eliminar", required = true) 
            @PathVariable String rut){
        servicio.eliminarProveedor(rut);
        return ResponseEntity.noContent().build();
    }
}