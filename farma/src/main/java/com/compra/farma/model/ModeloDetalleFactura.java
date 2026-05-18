package com.compra.farma.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "detalle_factura")

public class ModeloDetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleFactura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idFactura", nullable = false)
    @JsonIgnore
    private ModeloFactura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrdenCompra", nullable = false)
    private ModeloCompra compra;

    private Integer cantidad;

    private BigDecimal precioUnitario;

    private BigDecimal subTotal;

}
