package com.inventario.msinventario.exceptions;

public class StockInsuficienteException extends RuntimeException{

    public StockInsuficienteException(String mensaje){
        super(mensaje);
    }

}
