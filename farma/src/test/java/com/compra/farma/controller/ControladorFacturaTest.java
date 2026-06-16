package com.compra.farma.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.compra.farma.dto.DtoFactura;
import com.compra.farma.service.ServicioFactura;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

@WebMvcTest(ControladorFactura.class)
public class ControladorFacturaTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioFactura servicioFactura;

    @Test
    public void crear_DebeRetornar201() throws Exception {
        DtoFactura dtoMock = mock(DtoFactura.class);
        when(servicioFactura.crearFactura(any(DtoFactura.class))).thenReturn(dtoMock);

        String jsonFactura = "{\"idFactura\":1,\"numeroFactura\":12345,\"montoTotal\":50000.0,\"fechaEmision\":\"2026-06-16T17:44:00\"}";

        mockMvc.perform(post("/api/facturas")
                .header("Content-Type", "application/json")
                .content(jsonFactura))
                .andExpect(status().isCreated());
    }
    @Test
    public void listar_DebeRetornar200() throws Exception {
        when(servicioFactura.listarFacturas()).thenReturn(List.of());
        mockMvc.perform(get("/api/facturas")).andExpect(status().isOk());
    }
}