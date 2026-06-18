package com.compra.farma;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient (name = "microservicio-proveedor", url = "http://localhost:8085/api/proveedores")
public interface ProveedorClient {

    @GetMapping("/{rut}")
    Object obtenerProveedorPorRut(@PathVariable("rut") String rut);
}