package com.boleta.gestionboleta.client;

import com.boleta.gestionboleta.client.dto.ClienteRemotoDTO;
import com.boleta.gestionboleta.client.feign.ClienteBeneficioFeignClient;
import com.boleta.gestionboleta.excepcions.IntegracionExternaException;
import com.boleta.gestionboleta.excepcions.RecursoNoEncontradoException;

import feign.FeignException;

import org.springframework.stereotype.Component;

@Component
public class ClienteBeneficioClient {

    private final ClienteBeneficioFeignClient feignClient;

    public ClienteBeneficioClient(ClienteBeneficioFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    public ClienteRemotoDTO obtenerClientePorRun(String run) {
        try {
            return feignClient.obtenerClientePorRun(run);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con RUN: " + run);
        } catch (FeignException e) {
            throw new IntegracionExternaException(
                    "No fue posible obtener el cliente desde msClienteBeneficio. Estado: " + e.status());
        } catch (Exception e) {
            throw new IntegracionExternaException("No fue posible conectar con msClienteBeneficio.");
        }
    }

    public Integer obtenerDescuentoPorId(Long idBeneficio) {
        try {
            return feignClient.obtenerDescuentoPorId(idBeneficio);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Beneficio no encontrado con ID: " + idBeneficio);
        } catch (FeignException e) {
            throw new IntegracionExternaException(
                    "No fue posible obtener el descuento del beneficio desde msClienteBeneficio. Estado: "
                            + e.status());
        } catch (Exception e) {
            throw new IntegracionExternaException("No fue posible conectar con msClienteBeneficio.");
        }
    }
}
