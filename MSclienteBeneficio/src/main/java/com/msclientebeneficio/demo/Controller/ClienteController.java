package com.msclientebeneficio.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msclientebeneficio.demo.Dto.ClienteDTO;
import com.msclientebeneficio.demo.Service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Controlador para la gestión de datos de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/{run}")
    @Operation(summary = "Buscar cliente por RUN", description = "Recupera los detalles personales de un cliente utilizando su RUN único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClienteDTO> obtenerClientePorRun(@PathVariable String run) {
        return ResponseEntity.ok(clienteService.obtenerClientePorRun(run));
    }

    @PostMapping()
    @Operation(summary = "Crear nuevo cliente", description = "Registra un nuevo cliente con sus datos personales y beneficio asociado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o error de validación"),
        @ApiResponse(responseCode = "409", description = "Cliente duplicado detectado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClienteDTO> crearCliente(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del cliente a crear")
            @Valid @RequestBody ClienteDTO clienteDTO
    ) {
        ClienteDTO clienteCreado = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.status(201).body(clienteCreado);
    }

    @PutMapping("/{run}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza la información personal de un cliente existente identificado por su RUN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClienteDTO> actualizarCliente(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos para el cliente")
            @Valid @RequestBody ClienteDTO clienteDTO, 
            @PathVariable String run
    ) {
        return ResponseEntity.ok(clienteService.actualizarCliente(run, clienteDTO));
    }

    @DeleteMapping("/{run}/{dv}")
    @Operation(summary = "Eliminar cliente", description = "Elimina de forma permanente un cliente por su RUN y dígito verificador (DV).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarCliente(@PathVariable String run, @PathVariable String dv) {
        clienteService.eliminarCliente(run, dv);
        return ResponseEntity.noContent().build();
    }
}
