package com.compra.farma.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoDetalle {

    private Long idOrdenCompra;
    private Integer cantidad;
    private BigDecimal precioUnitario;

}
