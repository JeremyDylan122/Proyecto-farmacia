package com.compra.farma.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compra")

public class ModeloCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrdenCompra;

    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "El RUT del proveedor no tiene un formato válido")
    @NotBlank(message = "El Rut del proveedor no debe estar vacio")
    @Column(nullable = false)
    private String rutProveedor;

    @NotNull(message = "El sku es obligatorio")
    @Positive(message = "El sku debe ser mayor a cero")
    @Column(nullable = false)
    private Long sku;
    
    @NotNull(message = "la cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad minima de compra dbee ser al menos 1")
    @Column(nullable = false)
    private Long cantidad;

    @NotNull(message = "El código del lote es obligatorio") 
    @Column(nullable = false)
    private Long codigoLote;
    
    @NotNull(message = "La fecha de vencimiento es obligatoria")  
    @Future(message = "La fecha de vencimiento debe ser una fecha futura")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)     
    private Date fechaVencimiento;

    @NotNull(message = "El total de compra es obligatorio")
    @PositiveOrZero(message = "El total debe ser mayor o igual a cero")
    @Digits(integer = 10, fraction = 2, message = "El total de la compra no tiene un formato válido")
    @Column(nullable = false)
    private BigDecimal totalCompra;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura")
    private ModeloFactura factura;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ModeloDetalleFactura> detalles;

}