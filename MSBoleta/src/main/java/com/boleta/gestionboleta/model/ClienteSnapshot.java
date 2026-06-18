package com.boleta.gestionboleta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteSnapshot {

    @Column(name = "cliente_run", nullable = false, length = 8)
    private String run;

    @Column(name = "cliente_dv", nullable = false, length = 1)
    private String dv;

    @Column(name = "cliente_nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "cliente_apellido", nullable = false, length = 80)
    private String apellido;

    @Column(name = "cliente_correo", nullable = false, length = 80)
    private String correo;

    @Column(name = "cliente_id_beneficio")
    private Long idBeneficio;

    @Column(name = "cliente_descuento_entero", nullable = false)
    private Integer descuentoEntero;
}
