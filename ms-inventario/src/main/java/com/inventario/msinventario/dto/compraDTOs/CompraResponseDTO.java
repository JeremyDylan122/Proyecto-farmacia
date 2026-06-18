package com.inventario.msinventario.dto.compraDTOs;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompraResponseDTO {

    private Long idCompra;
    private Long sku; 
    private int cantidad;
    private String codigoLote;
    private LocalDate fechaVencimiento;


}
