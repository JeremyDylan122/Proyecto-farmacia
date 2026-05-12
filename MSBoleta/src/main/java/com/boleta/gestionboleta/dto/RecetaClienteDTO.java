package com.boleta.gestionboleta.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RecetaClienteDTO {

    private Long id;
    private String runCliente;
    private String tipoReceta;
    private String folioReceta;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private boolean activa;
}
