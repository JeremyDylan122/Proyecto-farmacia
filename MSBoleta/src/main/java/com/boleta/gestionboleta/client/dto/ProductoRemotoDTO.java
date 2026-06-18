package com.boleta.gestionboleta.client.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductoRemotoDTO {

    private Long sku;
    private String nombre;
    private BigDecimal precio;
    private String laboratorio;
    private String descripcion;
    private boolean activo;
    private String categoria;
    private String tipoReceta;
}
