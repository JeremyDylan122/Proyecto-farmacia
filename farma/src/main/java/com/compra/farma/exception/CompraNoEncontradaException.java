package com.compra.farma.exception;

public class CompraNoEncontradaException extends RuntimeException {
    public CompraNoEncontradaException(String idOrdenCompra) {
        super("Compra con id " + idOrdenCompra + " no encontrada");
    }
}
