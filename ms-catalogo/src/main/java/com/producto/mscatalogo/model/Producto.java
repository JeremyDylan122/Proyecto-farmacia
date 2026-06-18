package com.producto.mscatalogo.model;

import java.math.BigDecimal;

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
@Table(name = "producto")
public class Producto {

    @Id
    @Column(precision = 13)
    private Long sku;
    @Column(nullable = false, length = 255)
    private String nombre;
    @Column(nullable = false, precision = 9, scale = 2) 
    private BigDecimal precio;
    @Column(nullable = false, length = 50)
    private String laboratorio;
    @Column(length = 255)
    private String descripcion;
    @Column(name = "activo")
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "id_tipo_receta")
    private TipoReceta tipoReceta;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

}
