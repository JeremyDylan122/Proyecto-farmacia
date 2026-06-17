package com.inventario.msinventario.dto.movimientoDTOs;

import org.springframework.stereotype.Component;

import com.inventario.msinventario.model.Movimiento;
import com.inventario.msinventario.model.Stock;


@Component
public class MovimientoDTOMapper {

    public MovimientoDTO toDTO(Movimiento movimiento) {
        if (movimiento == null) {
            return null;
        }

        MovimientoDTO dto = new MovimientoDTO();
        dto.setIdMovimiento(movimiento.getIdMovimiento());
        dto.setCodigoLote(movimiento.getCodigoLote());
        dto.setCantidad(movimiento.getCantidad());
        dto.setReferenciaId(movimiento.getReferenciaId());
        dto.setFechaHora(movimiento.getFechaHora());
        dto.setTipo(movimiento.getTipo());

        if (movimiento.getStock() != null) {
            dto.setSku(movimiento.getStock().getSku());
        } else {
            dto.setSku(null);
        }

        return dto;
    }


    public Movimiento toModel(MovimientoDTO dto) {
        if (dto == null) {
            return null;
        }

        Movimiento movimiento = new Movimiento();
        movimiento.setIdMovimiento(dto.getIdMovimiento());
        movimiento.setCodigoLote(dto.getCodigoLote());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setReferenciaId(dto.getReferenciaId());
        movimiento.setTipo(dto.getTipo());
        if (dto.getFechaHora() != null) {
            movimiento.setFechaHora(dto.getFechaHora());
        }

        if (dto.getSku() != null) {
            Stock stockAsociado = new Stock();
            stockAsociado.setSku(dto.getSku());
            movimiento.setStock(stockAsociado);
        }

        return movimiento;
    }
}
