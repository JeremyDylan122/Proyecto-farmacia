package com.inventario.msinventario.dto.movimientoDTOs;

import java.time.LocalDateTime;

import com.inventario.msinventario.model.TipoMovimiento;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoDTO {

    private Long idMovimiento; // Puede ser nulo al crear (es autoincremental)

    @Schema(description="Identificador único de lote.", example="LT-2026-001")
    @NotBlank(message = "El código de lote es obligatorio para registrar el movimiento.")
    private String codigoLote;

    @Schema(description="Cantidad a registrar.", example="20")
    @NotNull(message = "La cantidad del movimiento es obligatoria.")
    @Min(value = 1, message = "La cantidad del movimiento debe ser igual o mayor a 1.")
    private Integer cantidad;

    @Schema(description="Identificador único para compra o venta.", example="123456789")
    @NotNull(message = "La referencia (ID de Venta o Compra) es obligatoria.")
    private Long referenciaId;

    @Schema(description="Hora y fecha del movimiento.", example="aaaa-mm-ddThh:mm:ss")
    private LocalDateTime fechaHora;

    @Schema(description="Tipo de movimiento.", example="COMPRA")
    @NotNull(message = "El tipo de movimiento (COMPRA, VENTA, MERMA) es obligatorio.")
    private TipoMovimiento tipo;

    @Schema(description="Identificador único de producto", example="780002")
    @NotNull(message = "El SKU del producto asociado es obligatorio.")
    private Long sku; // Guardamos solo la llave foránea como Long
}
