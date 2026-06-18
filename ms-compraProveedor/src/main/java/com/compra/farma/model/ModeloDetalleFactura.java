package com.compra.farma.model;

import java.math.BigDecimal;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
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
    @JsonBackReference
    private ModeloFactura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrdenCompra", nullable = false)
    @JsonBackReference
    private ModeloCompra compra;
    
    @NotNull(message = "El código del lote es obligatorio")
    @Column(nullable = false)
    private Long codigoLote;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad minima debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser una fecha futura")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fechaVencimiento;

    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario debe ser mayor o igual a cero")
    @Column(nullable = false)
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es obligatorio")
    @PositiveOrZero(message = "El subtotal debe ser mayor o igual a cero")
    @Column(nullable = false, precision = 10, scale = 2 )
    private BigDecimal subTotal;

}
