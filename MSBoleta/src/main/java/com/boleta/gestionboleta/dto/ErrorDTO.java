package com.boleta.gestionboleta.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estructura estándar de respuesta de error de la API")
public class ErrorDTO {

    @Schema(description = "Código de estado HTTP del error", example = "400", accessMode = Schema.AccessMode.READ_ONLY)
    private int status;

    @Schema(description = "Mensaje detallado explicando el error", example = "La cantidad consolidada para el SKU no puede ser mayor a 10 unidades.", accessMode = Schema.AccessMode.READ_ONLY)
    private String mensaje;

    @Schema(description = "Fecha y hora en la que ocurrió el error", example = "2026-06-03T18:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime timestamp;

    public ErrorDTO(int status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
