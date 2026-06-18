package com.inventario.msinventario.dto.compraDTOs;

import org.springframework.stereotype.Component;

import com.inventario.msinventario.dto.loteDTOs.LoteDTO;
import com.inventario.msinventario.model.Lote;
import com.inventario.msinventario.model.Stock;

@Component
public class CompraDTOMapper {

    //DATOS RECIBIDOS PARA CREAR UN MODELO
    public Lote toModel(CompraRequestDTO request){
        if (request == null){
            return null;
        }
        Lote lote = new Lote();
        lote.setIdCompra(request.getIdOrdenCompra());
        lote.setCantidad(request.getCantidad());
        lote.setCodigoLote(request.getCodigoLote());
        lote.setFechaVencimiento(request.getFechaVencimiento());
        Stock stock = new Stock();
        stock.setSku(request.getSku());
        lote.setStock(stock);

        return lote;     
    }

    //DATOS DEVUELTOS DE UN MODELO CREADO
    public CompraResponseDTO toResponse(LoteDTO lote) {
        if (lote == null) {
            return null;
        }
        CompraResponseDTO response = new CompraResponseDTO();
        //MAPPEO DE DATOS
        response.setIdCompra(lote.getIdCompra()); 
        response.setCantidad(lote.getCantidad());
        response.setCodigoLote(lote.getCodigoLote());
        response.setFechaVencimiento(lote.getFechaVencimiento());
        //response.setActivo(lote.isActivo());
        //NOS ENCARGAMOS QUE NO SEA NULL
        if (lote.getSku() != null) {
            response.setSku(lote.getSku());
        }

        return response;
    }
    
}