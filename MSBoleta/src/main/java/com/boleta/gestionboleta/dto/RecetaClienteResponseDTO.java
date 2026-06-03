package com.boleta.gestionboleta.dto;

import java.time.LocalDate;

import com.boleta.gestionboleta.model.RecetaCliente;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Datos de respuesta del registro de la receta médica de un cliente")
public class RecetaClienteResponseDTO {

    @Schema(description = "Identificador único del registro de la receta", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "RUN del cliente", example = "12345678", accessMode = Schema.AccessMode.READ_ONLY)
    private String runCliente;

    @Schema(description = "Tipo de receta registrada", example = "Receta Simple", accessMode = Schema.AccessMode.READ_ONLY)
    private String tipoReceta;

    @Schema(description = "Folio de la receta", example = "REC-98765", accessMode = Schema.AccessMode.READ_ONLY)
    private String folioReceta;

    @Schema(description = "Fecha de emisión de la receta", example = "2026-06-01", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate fechaEmision;

    @Schema(description = "Fecha de vencimiento de la receta", example = "2026-12-01", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate fechaVencimiento;

    @Schema(description = "Indica si la receta está activa y vigente para compras", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
    private boolean activa;

    public static RecetaClienteResponseDTO from(RecetaCliente recetaCliente) {
        if (recetaCliente == null) {
            return null;
        }

        RecetaClienteResponseDTO dto = new RecetaClienteResponseDTO();
        dto.setId(recetaCliente.getId());
        dto.setRunCliente(recetaCliente.getRunCliente());
        dto.setTipoReceta(recetaCliente.getTipoReceta());
        dto.setFolioReceta(recetaCliente.getFolioReceta());
        dto.setFechaEmision(recetaCliente.getFechaEmision());
        dto.setFechaVencimiento(recetaCliente.getFechaVencimiento());
        dto.setActiva(recetaCliente.isActiva());
        return dto;
    }
}
