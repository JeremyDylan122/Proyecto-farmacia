package com.inventario.msinventario.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.msinventario.dto.compraDTOs.CompraRequestDTO;
import com.inventario.msinventario.dto.compraDTOs.CompraResponseDTO;
import com.inventario.msinventario.dto.ventaDTOs.VentaRequestDTO;
import com.inventario.msinventario.dto.ventaDTOs.VentaResponseDTO;
import com.inventario.msinventario.exceptions.StockInsuficienteException;
import com.inventario.msinventario.service.InventarioService;

import feign.FeignException;

@WebMvcTest(InventarioController.class)
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventarioService inventarioService;

    private CompraRequestDTO crearRequestValido() {
        CompraRequestDTO request = new CompraRequestDTO();

        // Asignamos datos mínimos de prueba solo para que pasen tus anotaciones
        // @NotNull, @Min y @NotBlank
        request.setIdOrdenCompra(10245L);
        request.setSku(780001L);
        request.setCantidad(150); // Satisface @Min(1)
        request.setCodigoLote("LT-2026-001"); // Satisface @NotBlank
        request.setFechaVencimiento(java.time.LocalDate.of(2029, 12, 31)); // Satisface @NotNull

        return request;
    }

    @Test
    void whenRegistrarEntrada_thenReturnCreated() throws Exception {
        // GIVEN
        CompraRequestDTO request = crearRequestValido();
        CompraResponseDTO response = new CompraResponseDTO();

        // WHEN
        when(inventarioService.registrarCompra(any(CompraRequestDTO.class))).thenReturn(response);

        // THEN
        mockMvc.perform(post("/api/v1/inventario/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void whenRegistrarEntrada_throwBadRequest_byMalformedJson() throws Exception {
        // GIVEN
        String jsonInvalido = "{ idCompra: , sku: loquesea }";

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/inventario/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenRegistrarEntrada_throwInternalServerError_byFeignException() throws Exception {
        // GIVEN
        CompraRequestDTO request = crearRequestValido();

        // Creamos un mock de la excepción FeignException
        FeignException feignExceptionMock = org.mockito.Mockito.mock(FeignException.class);

        // WHEN el servicio intente registrar, lanzará el error de comunicación de Feign
        when(inventarioService.registrarCompra(any(CompraRequestDTO.class)))
                .thenThrow(feignExceptionMock);

        // THEN
        mockMvc.perform(post("/api/v1/inventario/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()); // 👈 Atrapado por tu handleFeignException -> 500
    }

    // ------------------- TEST VENTA --------------------------

    private VentaRequestDTO crearVentaRequestValido() {
        VentaRequestDTO request = new VentaRequestDTO();
        request.setSku(780001L);
        request.setCantidad(6);
        request.setIdVenta(10245L);
        return request;
    }

    @Test
    void whenValidarVenta_thenReturnOk() throws Exception {
        // GIVEN
        VentaRequestDTO request = crearVentaRequestValido();
        VentaResponseDTO response = new VentaResponseDTO(); // Cascarón vacío

        // WHEN
        when(inventarioService.procesarVenta(any(VentaRequestDTO.class))).thenReturn(response);

        // THEN
        mockMvc.perform(post("/api/v1/inventario/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void whenValidarVenta_throwBadRequest_byStockInsuficiente() throws Exception {
        // GIVEN
        VentaRequestDTO request = crearVentaRequestValido();

        // WHEN
        when(inventarioService.procesarVenta(any(VentaRequestDTO.class)))
                .thenThrow(new StockInsuficienteException("Stock insuficiente"));

        // THEN
        mockMvc.perform(post("/api/v1/inventario/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Valida que el flujo corte en 400
    }

    @Test
    void whenValidarVenta_throwInternalServerError_byFeignException() throws Exception {
        // GIVEN
        VentaRequestDTO request = crearVentaRequestValido();
        
        // Mockeamos el cascarón de la excepción de la librería externa Feign
        feign.FeignException feignExceptionMock = org.mockito.Mockito.mock(feign.FeignException.class);

        //WHEN
        when(inventarioService.procesarVenta(any(VentaRequestDTO.class)))
                .thenThrow(feignExceptionMock);

        // THEN
        mockMvc.perform(post("/api/v1/inventario/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()); // Valida que tu handleFeignException lo traduzca a 500
    }
}
