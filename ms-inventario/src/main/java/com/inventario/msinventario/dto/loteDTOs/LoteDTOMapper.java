package com.inventario.msinventario.dto.loteDTOs;

import org.springframework.stereotype.Component;

import com.inventario.msinventario.model.Lote;
import com.inventario.msinventario.model.Stock;

@Component
public class LoteDTOMapper {

    public LoteDTO toDTO(Lote lote) {
        if (lote == null) {
            return null;
        }

        LoteDTO dto = new LoteDTO();
        dto.setCodigoLote(lote.getCodigoLote());
        dto.setCantidad(lote.getCantidad());
        dto.setFechaVencimiento(lote.getFechaVencimiento());
        dto.setIdCompra(lote.getIdCompra());
        //dto.setActivo(lote.isActivo());

        if (lote.getStock() != null) {
            dto.setSku(lote.getStock().getSku());
        } else {
            dto.setSku(null);
        }

        return dto;
    }

    public Lote toModel(LoteDTO dto) {
        if (dto == null) {
            return null;
        }

        Lote lote = new Lote();
        lote.setCodigoLote(dto.getCodigoLote());
        lote.setCantidad(dto.getCantidad());
        lote.setFechaVencimiento(dto.getFechaVencimiento());
        lote.setIdCompra(dto.getIdCompra());

        if (dto.getSku() != null) {
            Stock stockAsociado = new Stock();
            stockAsociado.setSku(dto.getSku());
            lote.setStock(stockAsociado);
        }

        return lote;
    }

}
