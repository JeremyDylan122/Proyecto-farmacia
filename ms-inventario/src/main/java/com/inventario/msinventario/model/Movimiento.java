package com.inventario.msinventario.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "movimiento_inventario")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    @Column(nullable = false, length = 50)
    private String codigoLote;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Long referenciaId; // idVenta (Jeremy) o idOrdenCompra (Jonatan)

    @Column(nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo; // COMPRA, VENTA, MERMA

    @ManyToOne
    @JoinColumn(name = "sku") 
    private Stock stock;
    
}
