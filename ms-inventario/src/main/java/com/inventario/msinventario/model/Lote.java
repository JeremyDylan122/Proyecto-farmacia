package com.inventario.msinventario.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

// GRUPO DE PRODUCTOS QUE COMPARTEN FECHA, PARA APLICAR FEFO (first expired, first out).

@Table(name = "lote")
public class Lote {             

    @Id
    @Column(name = "codigo_lote",length = 50)
    private String codigoLote;

    @Column(name = "cantidad_lote", nullable = false)
    private Integer cantidad;

    @Column(name = "fecha_vencimiento",nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "id_orden_compra", nullable = false)
    private Long idCompra;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "sku")
    @JsonIgnore
    private Stock stock;
}


