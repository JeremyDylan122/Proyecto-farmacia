package com.boleta.gestionboleta.client;

import org.springframework.stereotype.Component;

import com.boleta.gestionboleta.client.dto.ProductoRemotoDTO;
import com.boleta.gestionboleta.client.feign.InventarioFeignClient;
import com.boleta.gestionboleta.excepcions.IntegracionExternaException;
import com.boleta.gestionboleta.excepcions.RecursoNoEncontradoException;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InventarioClient {

    private final InventarioFeignClient feignClient;

    public InventarioClient(InventarioFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    public ProductoRemotoDTO obtenerProductoPorSku(Long sku) {
        try {
            log.info("Consultando producto en msGestionInventario. sku={}", sku);
            return feignClient.obtenerProductoPorSku(sku);
        } catch (FeignException.NotFound e) {
            log.warn("Producto no encontrado en msGestionInventario. sku={}", sku);
            throw new RecursoNoEncontradoException("Producto no encontrado con SKU: " + sku);
        } catch (FeignException e) {
            log.error("Error consultando producto en msGestionInventario. sku={}, status={}", sku, e.status(), e);
            throw new IntegracionExternaException(
                    "No fue posible obtener el producto desde msGestionInventario. Estado: " + e.status());
        } catch (Exception e) {
            log.error("Fallo de conectividad con msGestionInventario al consultar producto. sku={}", sku, e);
            throw new IntegracionExternaException("No fue posible conectar con msGestionInventario.");
        }
    }
}
