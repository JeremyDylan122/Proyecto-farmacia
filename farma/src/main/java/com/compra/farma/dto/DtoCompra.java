package com.compra.farma.dto;

import java.math.BigDecimal;
import java.sql.Date;

import jakarta.validation.constraints.*;

public record DtoCompra(

    @Positive(message = "El id de orden de compra debe ser positivo")
    Long idOrdenCompra,

    @NotBlank(message = "El rut del proveedor es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "El RUT del proveedor no tiene un formato válido")
    String rutProveedor,

    @NotNull(message = "El sku es obligatorio")
    @Positive(message = "El sku debe ser mayor a 0")
    Long sku,

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad minima es 1")
    Long cantidad,

    @NotNull(message = "El total de compra es obligatoria")
    @PositiveOrZero(message = "El total de compra debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El total de la compra no tiene un formato valido")
    BigDecimal totalCompra,

    @NotNull(message = "El código del lote es obligatorio")
    Long codigoLote,

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser una fecha futura")
    Date fechaVencimiento,

    DtoFactura factura,

    Object proveedor

){}

