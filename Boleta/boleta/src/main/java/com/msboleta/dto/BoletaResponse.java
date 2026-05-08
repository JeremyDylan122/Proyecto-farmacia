package com.msboleta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoletaResponse {
    private Long id;
    private String folio;
    private String runCliente;
    private String dvCliente;
    private String clienteNombre;
    private BigDecimal porcentajeDescuento;
    private BigDecimal montoNeto;
    private BigDecimal iva;
    private BigDecimal montoBruto;
    private LocalDateTime fechaEmision;
    private boolean anulada;
    private List<BoletaItemResponse> productos;
}