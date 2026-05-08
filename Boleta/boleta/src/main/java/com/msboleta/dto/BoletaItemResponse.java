package com.msboleta.dto;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoletaItemResponse {
    private Long sku;
    private String nombreProducto;
    private String tipoReceta;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
