package com.producto.mscatalogo.exceptions;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.producto.mscatalogo.dto.ErrorDTO;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDTO> handleRecursoNoEncontrado(RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorDTO> handleRecursoDuplicado(RecursoDuplicadoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorDTO(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(RecursoNuloException.class)
    public ResponseEntity<ErrorDTO> handleRecursoNulo(RecursoNuloException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}
