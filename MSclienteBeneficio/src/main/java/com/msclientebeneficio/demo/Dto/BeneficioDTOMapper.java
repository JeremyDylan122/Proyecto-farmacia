package com.msclientebeneficio.demo.Dto;

import org.springframework.stereotype.Component;

import com.msclientebeneficio.demo.Model.Beneficio;

@Component

public class BeneficioDTOMapper {

    public BeneficioDTO toDTO(Beneficio beneficio) {
        if (beneficio == null) {
            return null;
        }

        BeneficioDTO beneficioDTO = new BeneficioDTO();
        beneficioDTO.setId(beneficio.getId());
        beneficioDTO.setNombre(beneficio.getNombre());
        beneficioDTO.setDescuento(beneficio.getDescuento());
        return beneficioDTO;
    }
}
