package com.compra.farma.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoFactura {

    @NotBlank
    private String rutCliente;

    @NotBlank
    private String nombreCliente;

    @NotEmpty
    private List<DtoDetalle> detalles;



}
