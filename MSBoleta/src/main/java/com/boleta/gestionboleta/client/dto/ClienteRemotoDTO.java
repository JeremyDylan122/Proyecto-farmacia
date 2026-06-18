package com.boleta.gestionboleta.client.dto;

import lombok.Data;

@Data
public class ClienteRemotoDTO {

    private String run;
    private String dv;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private Long idBeneficio;
}
