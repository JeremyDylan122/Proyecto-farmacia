package com.inventario.msinventario.dto.ventaDTOs;

import org.springframework.stereotype.Component;

import com.inventario.msinventario.dto.stockDTOs.StockDTO;

@Component
public class VentaDTOMapper {

    public VentaResponseDTO toResponse(StockDTO stockDTO, int cantidadVendida) {
        if (stockDTO == null) {
            return null;
        }

        VentaResponseDTO response = new VentaResponseDTO();
        response.setSku(stockDTO.getSku());
        response.setCantidadVendida(cantidadVendida);
        response.setEstado("APROBADO");

        return response;
    }
}
