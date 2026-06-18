package com.msclientebeneficio.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msclientebeneficio.demo.Dto.BeneficioDTO;
import com.msclientebeneficio.demo.Dto.BeneficioDTOMapper;
import com.msclientebeneficio.demo.Model.Beneficio;
import com.msclientebeneficio.demo.Service.BeneficioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/beneficios")
@RequiredArgsConstructor
@Tag(name = "Beneficios", description = "Controlador para gestionar beneficios y descuentos de clientes")
public class BeneficioController {

    private final BeneficioService beneficioService;
    private final BeneficioDTOMapper beneficioDTOMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar beneficio por ID", description = "Obtiene los detalles de un beneficio específico por su identificador único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Beneficio encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Beneficio no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BeneficioDTO> obtenerBeneficioPorId(@PathVariable Long id) {
        Beneficio beneficio = beneficioService.findById(id);
        return ResponseEntity.ok(beneficioDTOMapper.toDTO(beneficio));
    }

    @GetMapping("/{id}/descuento")
    @Operation(summary = "Obtener descuento de beneficio por ID", description = "Obtiene el porcentaje de descuento asignado a un beneficio específico por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Descuento obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Beneficio no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Integer> obtenerDescuentoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(beneficioService.obtenerDescuentoPorId(id));
    }
}
