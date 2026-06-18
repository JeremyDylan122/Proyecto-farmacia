package com.compra.farma.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity     
@Table(name = "factura")
@AllArgsConstructor
@NoArgsConstructor
public class ModeloFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;

    @Column(nullable = false, unique = true)
    private String numeroFactura;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @NotBlank(message = "El RUT del cliente es obligatorio")
    @Column(nullable = false)
    private String rutCliente;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Column(nullable = false)
    private String nombreCliente;

    @NotNull(message = "El total de la factura es obligatorio")
    @PositiveOrZero(message = "El total de la factura debe ser mayor o igual a cero")
    private BigDecimal totalFactura;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModeloDetalleFactura> detalles = new ArrayList<>();
}
