package com.boleta.gestionboleta.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para emitir una nueva boleta de venta")
public class CrearBoletaRequestDTO {

    @NotBlank(message = "Debe ingresar el RUN del cliente.")
    @Size(min = 8, max = 8, message = "El RUN debe tener exactamente 8 digitos.")
    @Pattern(regexp = "\\d{8}", message = "El RUN debe contener solo digitos.")
    @Schema(description = "RUN del cliente titular (sin puntos ni guion, exactamente 8 dígitos)", example = "12345678", required = true, minLength = 8, maxLength = 8, pattern = "\\d{8}")
    private String runCliente;

    @Valid
    @Size(min = 1, message = "Debe agregar al menos un producto a la boleta.")
    @Schema(description = "Listado de productos a incluir en la boleta (mínimo 1)", required = true)
    private List<BoletaProductoRequestDTO> productos;
}
