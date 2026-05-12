package com.boleta.gestionboleta.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "boleta")
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long folio;

    @Embedded
    private ClienteSnapshot cliente;

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<BoletaDetalle> productos = new ArrayList<>();

    @Column(name = "porcentaje_descuento", nullable = false, precision = 5, scale = 4)
    private BigDecimal porcentajeDescuento;

    @Column(name = "monto_descuento", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoDescuento;

    @Column(name = "monto_neto", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoNeto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal iva;

    @Column(name = "monto_bruto", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoBruto;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private boolean anulada;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    public void limpiarProductos() {
        productos.clear();
    }

    public void agregarProducto(BoletaDetalle detalle) {
        detalle.setBoleta(this);
        productos.add(detalle);
    }
}
