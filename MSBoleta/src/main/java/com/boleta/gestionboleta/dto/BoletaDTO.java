package com.boleta.gestionboleta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class BoletaDTO {

    private Long id;
    private Long folio;
    private ClienteBoletaDTO cliente;
    private List<BoletaDetalleDTO> productos;
    private BigDecimal porcentajeDescuento;
    private BigDecimal montoDescuento;
    private BigDecimal montoNeto;
    private BigDecimal iva;
    private BigDecimal montoBruto;
    private LocalDateTime fechaEmision;
    private boolean anulada;
}
