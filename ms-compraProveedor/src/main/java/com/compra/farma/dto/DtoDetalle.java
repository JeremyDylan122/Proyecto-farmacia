package com.compra.farma.dto;

import java.math.BigDecimal;
import java.time.LocalDate; 

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoDetalle {

    private String idOrdenCompra;

    @NotNull(message = "La cantidad es obligatoria") 
    @Min(value = 1, message = "La cantidad minima es 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario debe ser mayor o igual a 0")  
    @Digits(integer = 10, fraction = 2, message = "El precio unitario no tiene un formato valido")  
    private BigDecimal precioUnitario;

    private String codigoLote;

    private LocalDate fechaVencimiento;

}