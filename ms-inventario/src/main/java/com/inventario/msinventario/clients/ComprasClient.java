package com.inventario.msinventario.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.inventario.msinventario.dto.compraDTOs.CompraRequestDTO;

@FeignClient(name = "ms-compraProveedor", url = "http://localhost:8086")
public interface ComprasClient {

    @GetMapping("/api/compras/{idOrdenCompra}")
    CompraRequestDTO obtenerCompra(@PathVariable("idOrdenCompra") Long idOrdenCompra);

}
