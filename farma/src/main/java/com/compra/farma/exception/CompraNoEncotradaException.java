package com.compra.farma.exception;

public class CompraNoEncotradaException extends RuntimeException {
    public CompraNoEncotradaException(Long idOrdenCompra) {
        super("Compra con id " + idOrdenCompra + " no encontrada");
    }
}
