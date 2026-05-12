package com.boleta.gestionboleta.dto;

import lombok.Data;

@Data
public class ClienteBoletaDTO {

    private String run;
    private String dv;
    private String nombre;
    private String apellido;
    private String correo;
    private Long idBeneficio;
    private Integer descuentoEntero;
}
