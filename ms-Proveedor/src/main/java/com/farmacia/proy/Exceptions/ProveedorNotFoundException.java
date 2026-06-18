package com.farmacia.proy.Exceptions;

public class ProveedorNotFoundException extends RuntimeException {

    public ProveedorNotFoundException(String rut) {
        super("Proveedor no encontrado con RUT: " + rut);
    }

}
