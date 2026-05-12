package com.boleta.gestionboleta.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BoletaDetalleDTO {

    private Long skuProducto;
    private String nombreProducto;
    private String tipoReceta;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal montoLinea;
}
