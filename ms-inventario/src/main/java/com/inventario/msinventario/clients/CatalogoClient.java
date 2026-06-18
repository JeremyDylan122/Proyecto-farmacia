package com.inventario.msinventario.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "catalogo-service",url = "http://localhost:8081")
public interface CatalogoClient {
    
    @PutMapping("/api/v1/productos/{sku}/visible")
    void cambiarVisibilidad(@PathVariable("sku") Long sku, @RequestParam("activo") boolean activo);


}
