package com.boleta.gestionboleta.client;

import com.boleta.gestionboleta.client.dto.ProductoRemotoDTO;
import com.boleta.gestionboleta.client.feign.InventarioFeignClient;
import com.boleta.gestionboleta.excepcions.IntegracionExternaException;
import com.boleta.gestionboleta.excepcions.RecursoNoEncontradoException;

import feign.FeignException;

import org.springframework.stereotype.Component;

@Component
public class InventarioClient {

    private final InventarioFeignClient feignClient;

    public InventarioClient(InventarioFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    public ProductoRemotoDTO obtenerProductoPorSku(Long sku) {
        try {
            return feignClient.obtenerProductoPorSku(sku);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado con SKU: " + sku);
        } catch (FeignException e) {
            throw new IntegracionExternaException(
                    "No fue posible obtener el producto desde msGestionInventario. Estado: " + e.status());
        } catch (Exception e) {
            throw new IntegracionExternaException("No fue posible conectar con msGestionInventario.");
        }
    }
}
