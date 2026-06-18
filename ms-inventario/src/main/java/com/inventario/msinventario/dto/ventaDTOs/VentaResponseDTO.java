package com.inventario.msinventario.dto.ventaDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaResponseDTO {

    private Long sku;
    private Integer cantidadVendida; 
    private String estado;
    
}
