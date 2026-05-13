package com.boleta.gestionboleta.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.boleta.gestionboleta.client.dto.ClienteRemotoDTO;

@FeignClient(name = "clienteBeneficioFeignClient", url = "http://localhost:8083")
public interface ClienteBeneficioFeignClient {

    @GetMapping("/api/clientes/{run}")
    ClienteRemotoDTO obtenerClientePorRun(@PathVariable("run") String run);

    @GetMapping("/api/beneficios/{id}/descuento")
    Integer obtenerDescuentoPorId(@PathVariable("id") Long idBeneficio);
}
