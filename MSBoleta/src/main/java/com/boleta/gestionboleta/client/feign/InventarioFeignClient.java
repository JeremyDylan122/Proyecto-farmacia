package com.boleta.gestionboleta.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.boleta.gestionboleta.client.dto.ProductoRemotoDTO;

@FeignClient(name = "inventarioFeignClient", url = "${inventario.api.base-url}")
public interface InventarioFeignClient {

    @GetMapping("/api/v1/productos/{sku}")
    ProductoRemotoDTO obtenerProductoPorSku(@PathVariable("sku") Long sku);
}
