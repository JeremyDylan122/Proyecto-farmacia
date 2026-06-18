package com.boleta.gestionboleta.excepcions;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.boleta.gestionboleta.dto.ErrorDTO;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDTO> handleRecursoNoEncontrado(RecursoNoEncontradoException e) {
        log.warn("Recurso no encontrado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorDTO> handleRecursoDuplicado(RecursoDuplicadoException e) {
        log.warn("Recurso duplicado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorDTO(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler({ RecursoNuloException.class, ReglaNegocioException.class })
    public ResponseEntity<ErrorDTO> handleBadRequest(RuntimeException e) {
        log.warn("Regla de negocio o dato invalido: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(IntegracionExternaException.class)
    public ResponseEntity<ErrorDTO> handleIntegracionExterna(IntegracionExternaException e) {
        log.error("Error de integracion externa: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorDTO(HttpStatus.BAD_GATEWAY.value(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidacion(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .collect(Collectors.joining(" | "));

        log.warn("Error de validacion: {}", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), mensaje));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("Mensaje HTTP no legible: {}", e.getMessage());
        String mensaje = "El formato de los datos en la solicitud es invalido.";

        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            String targetType = ife.getTargetType().getSimpleName();
            String fieldName = ife.getPath().stream()
                    .map(Reference::getFieldName)
                    .collect(Collectors.joining("."));

            if ("LocalDate".equals(targetType) || "LocalDateTime".equals(targetType)) {
                mensaje = "El formato de fecha para el campo '" + fieldName + "' es invalido. Asegurese de usar el formato YYYY-MM-DD.";
            } else if ("Integer".equals(targetType) || "Long".equals(targetType)) {
                mensaje = "El valor para el campo '" + fieldName + "' es invalido. Asegurese de ingresar un numero entero sin decimales y no negativo.";
            } else {
                mensaje = "El valor ingresado para el campo '" + fieldName + "' no corresponde al tipo esperado (" + targetType + ").";
            }
        } else if (cause instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) cause;
            String targetType = mie.getTargetType().getSimpleName();
            String fieldName = mie.getPath().stream()
                    .map(Reference::getFieldName)
                    .collect(Collectors.joining("."));

            if ("Integer".equals(targetType) || "Long".equals(targetType)) {
                mensaje = "El valor para el campo '" + fieldName + "' es invalido. Asegurese de ingresar un numero entero sin decimales.";
            } else {
                mensaje = "El tipo de dato para el campo '" + fieldName + "' es invalido. Se esperaba: " + targetType + ".";
            }
        } else if (e.getMessage() != null && e.getMessage().contains("LocalDate")) {
            mensaje = "Formato de fecha invalido. Asegurese de usar el formato YYYY-MM-DD.";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), mensaje));
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ErrorDTO> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException e) {
        log.warn("Recurso estatico o endpoint no encontrado: {}", e.getMessage());
        String mensaje = "El recurso solicitado no fue encontrado. Verifique que la URL y el ID ingresados sean correctos.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(HttpStatus.NOT_FOUND.value(), mensaje));
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException e) {
        log.warn("Tipo de argumento de metodo no coincide: {}", e.getMessage());
        String mensaje = "El valor '" + e.getValue() + "' es invalido para el parametro '" + e.getName() + "'. Se esperaba un tipo " + e.getRequiredType().getSimpleName() + ".";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleExcepcionGeneral(Exception e) {
        log.error("Error inesperado no controlado.", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Ocurrio un error inesperado: " + e.getMessage()));
    }
}
