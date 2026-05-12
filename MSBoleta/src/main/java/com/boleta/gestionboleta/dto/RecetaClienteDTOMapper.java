package com.boleta.gestionboleta.dto;

import org.springframework.stereotype.Component;

import com.boleta.gestionboleta.model.RecetaCliente;

@Component
public class RecetaClienteDTOMapper {

    public RecetaClienteDTO toDTO(RecetaCliente recetaCliente) {
        if (recetaCliente == null) {
            return null;
        }

        RecetaClienteDTO dto = new RecetaClienteDTO();
        dto.setId(recetaCliente.getId());
        dto.setRunCliente(recetaCliente.getRunCliente());
        dto.setTipoReceta(recetaCliente.getTipoReceta());
        dto.setFolioReceta(recetaCliente.getFolioReceta());
        dto.setFechaEmision(recetaCliente.getFechaEmision());
        dto.setFechaVencimiento(recetaCliente.getFechaVencimiento());
        dto.setActiva(recetaCliente.isActiva());
        return dto;
    }
}
