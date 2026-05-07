package com.msclientebeneficio.demo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @Column(nullable=false, length=8)
    private String run;
    @Column(nullable=false, length=1)
    private String dv;
    @Column(nullable=false, length=80)
    private String nombre;
    @Column(nullable=false, length=80)
    private String apellido;
    @Column(nullable=false, length=80)
    private String correo;
    @Column(nullable=false, length=9)
    private String telefono;
    @OneToOne
    @JoinColumn(name = "id_beneficio", referencedColumnName = "id")
    private Beneficio beneficio;
}
