package com.msboleta.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "boleta", schema = "boleta_bd")
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String folio;

    @Column(name = "run_cliente", nullable = false, length = 12)
    private String runCliente;

    @Column(name = "dv_cliente", nullable = false, length = 2)
    private String dvCliente;

    @Column(name = "cliente_nombre", nullable = false, length = 160)
    private String clienteNombre;

    @Column(name = "porcentaje_descuento", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(name = "monto_neto", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoNeto;

    @Column(name = "iva", nullable = false, precision = 12, scale = 2)
    private BigDecimal iva;

    @Column(name = "monto_bruto", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoBruto;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private boolean anulada;

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BoletaItem> items = new ArrayList<>();
}
