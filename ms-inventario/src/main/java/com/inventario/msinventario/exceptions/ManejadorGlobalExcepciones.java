package com.inventario.msinventario.exceptions;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.inventario.msinventario.dto.ErrorDTO;

import feign.FeignException;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNuloException.class)
    public ResponseEntity<ErrorDTO> handleRecursoNulo(RecursoNuloException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorDTO> handleStockInsuficiente(StockInsuficienteException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDTO> handleRecursoNoEncontrado(RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        String mensajesDeError = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(" "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), mensajesDeError));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorDTO> handleFeignException(FeignException e) {
        String mensaje = "Error de comunicación: El servicio no está disponible o rechazó la petición.";

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), mensaje));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        String mensaje = "Error en el formato del JSON: Uno o más campos contienen un tipo de dato incorrecto.";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), mensaje));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String mensaje = String.format("El parámetro '%s' debe ser un número válido.", e.getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), mensaje));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleRuntimeException(RuntimeException e) {

        String mensaje = e.getMessage();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), mensaje));
    }


}
