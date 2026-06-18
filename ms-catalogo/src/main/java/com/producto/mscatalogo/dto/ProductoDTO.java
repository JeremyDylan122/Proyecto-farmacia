package com.producto.mscatalogo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoDTO {

    @NotNull(message = "El sku del producto es obligatorio")
    private Long sku;

    @NotBlank(message = "El nombre del producto es obligatorio.")
    private String nombre;

    @NotNull(message = "El precio del producto es obligatorio.")
    private BigDecimal precio;

    @NotBlank(message = "El fabricante del producto es obligatorio.")
    private String laboratorio;

    private String descripcion;

    private boolean activo;

    @NotBlank(message = "El producto debe pertenecer a una categoria.")
    private String categoria;

    @NotBlank(message = "Debe especificar el tipo de receta del producto.")
    private String tipoReceta;

}
