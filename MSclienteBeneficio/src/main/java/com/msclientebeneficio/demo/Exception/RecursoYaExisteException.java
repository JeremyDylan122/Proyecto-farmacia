package com.msclientebeneficio.demo.Exception;

public class RecursoYaExisteException extends RuntimeException {

    public RecursoYaExisteException(String mensaje) {
        super(mensaje);
    }
    
}