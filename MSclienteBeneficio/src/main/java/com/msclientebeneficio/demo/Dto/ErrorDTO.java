package com.msclientebeneficio.demo.Dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información del error producido en la petición")
public class ErrorDTO {

    @Schema(description = "Código de estado HTTP", example = "400", accessMode = Schema.AccessMode.READ_ONLY)
    private int status;

    @Schema(description = "Descripción del error", example = "El run no puede estar vacío.", accessMode = Schema.AccessMode.READ_ONLY)
    private String mensaje;

    @Schema(description = "Instante de tiempo del suceso", example = "2026-06-03T18:00:00", accessMode = Schema.AccessMode.READ_ONLY)
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