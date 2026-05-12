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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/beneficios")
@RequiredArgsConstructor
public class BeneficioController {

    private final BeneficioService beneficioService;
    private final BeneficioDTOMapper beneficioDTOMapper;

    @GetMapping("/{id}")
    public ResponseEntity<BeneficioDTO> obtenerBeneficioPorId(@PathVariable Long id) {
        Beneficio beneficio = beneficioService.findById(id);
        return ResponseEntity.ok(beneficioDTOMapper.toDTO(beneficio));
    }

    @GetMapping("/{id}/descuento")
    public ResponseEntity<Integer> obtenerDescuentoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(beneficioService.obtenerDescuentoPorId(id));
    }
}
