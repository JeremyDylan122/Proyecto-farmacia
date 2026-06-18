package com.inventario.msinventario.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

// TABLA DE ALMACENAMIENTO FISICO DE LOS PRODUCTOS. El total almacenados.

@Table(name = "inventario_stock")
public class Stock {

    @Id
    @Column(precision = 13)
    private Long sku;

    @Column(nullable = false)
    private Integer cantidadTotal;

    @Column(nullable = false, length = 100)
    private String ubicacionBodega;

    @OneToMany(mappedBy = "stock")
    private List<Lote> lotes;
    
}
