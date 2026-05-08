package com.msboleta.dto;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteRecetaResponse {
    private Long id;
    private String runCliente;
    private String tipoReceta;
    private LocalDateTime fechaAsignacion;
}

