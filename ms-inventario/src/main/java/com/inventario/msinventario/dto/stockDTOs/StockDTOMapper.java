package com.inventario.msinventario.dto.stockDTOs;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.inventario.msinventario.dto.loteDTOs.LoteDTO;
import com.inventario.msinventario.dto.loteDTOs.LoteDTOMapper;
import com.inventario.msinventario.model.Stock;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class StockDTOMapper {

    private final LoteDTOMapper loteMapper;

    public StockDTO toDTO(Stock stock) {
        if (stock == null) {
            return null;
        }

        StockDTO dto = new StockDTO();
        dto.setSku(stock.getSku());
        dto.setCantidadTotal(stock.getCantidadTotal());
        dto.setUbicacionBodega(stock.getUbicacionBodega());

        if (stock.getLotes() == null) {
            dto.setLotes(new ArrayList<>());
        } else {
            List<LoteDTO> listaLotesDTO = stock.getLotes().stream()
                    .map(loteMapper::toDTO)
                    .toList();
            dto.setLotes(listaLotesDTO);
        }

        return dto;
    }

    // 2. De DTO (Vista) a Modelo (BD) - AQUÍ APLICAMOS LA REGLA DE NEGOCIO
    public Stock toModel(StockDTO dto) {
        if (dto == null) {
            return null;
        }

        Stock stock = new Stock();
        stock.setSku(dto.getSku());
        stock.setCantidadTotal(dto.getCantidadTotal());

        // APLICACIÓN DE REGLA DE NEGOCIO: "RECEPCION" por defecto
        if (dto.getUbicacionBodega() == null || dto.getUbicacionBodega().trim().isEmpty()) {
            stock.setUbicacionBodega("RECEPCION");
        } else {
            stock.setUbicacionBodega(dto.getUbicacionBodega().trim());
        }

        stock.setLotes(new ArrayList<>()); 

        return stock;
    }

}
