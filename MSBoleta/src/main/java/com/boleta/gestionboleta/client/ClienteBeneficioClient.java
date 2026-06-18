package com.boleta.gestionboleta.client;

import org.springframework.stereotype.Component;

import com.boleta.gestionboleta.client.dto.ClienteRemotoDTO;
import com.boleta.gestionboleta.client.feign.ClienteBeneficioFeignClient;
import com.boleta.gestionboleta.excepcions.IntegracionExternaException;
import com.boleta.gestionboleta.excepcions.RecursoNoEncontradoException;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ClienteBeneficioClient {

    private final ClienteBeneficioFeignClient feignClient;

    public ClienteBeneficioClient(ClienteBeneficioFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    public ClienteRemotoDTO obtenerClientePorRun(String run) {
        try {
            log.info("Consultando cliente en msClienteBeneficio. run={}", run);
            return feignClient.obtenerClientePorRun(run);
        } catch (FeignException.NotFound e) {
            log.warn("Cliente no encontrado en msClienteBeneficio. run={}", run);
            throw new RecursoNoEncontradoException("Cliente no encontrado con RUN: " + run);
        } catch (FeignException e) {
            log.error("Error consultando cliente en msClienteBeneficio. run={}, status={}", run, e.status(), e);
            throw new IntegracionExternaException(
                    "No fue posible obtener el cliente desde msClienteBeneficio. Estado: " + e.status());
        } catch (Exception e) {
            log.error("Fallo de conectividad con msClienteBeneficio al consultar cliente. run={}", run, e);
            throw new IntegracionExternaException("No fue posible conectar con msClienteBeneficio.");
        }
    }

    public Integer obtenerDescuentoPorId(Long idBeneficio) {
        try {
            log.info("Consultando descuento de beneficio en msClienteBeneficio. idBeneficio={}", idBeneficio);
            return feignClient.obtenerDescuentoPorId(idBeneficio);
        } catch (FeignException.NotFound e) {
            log.warn("Beneficio no encontrado en msClienteBeneficio. idBeneficio={}", idBeneficio);
            throw new RecursoNoEncontradoException("Beneficio no encontrado con ID: " + idBeneficio);
        } catch (FeignException e) {
            log.error("Error consultando descuento de beneficio en msClienteBeneficio. idBeneficio={}, status={}",
                    idBeneficio, e.status(), e);
            throw new IntegracionExternaException(
                    "No fue posible obtener el descuento del beneficio desde msClienteBeneficio. Estado: "
                            + e.status());
        } catch (Exception e) {
            log.error("Fallo de conectividad con msClienteBeneficio al consultar descuento. idBeneficio={}",
                    idBeneficio, e);
            throw new IntegracionExternaException("No fue posible conectar con msClienteBeneficio.");
        }
    }
}
