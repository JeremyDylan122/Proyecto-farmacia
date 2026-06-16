package com.compra.farma.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.compra.farma.dto.DtoDetalle;
import com.compra.farma.service.ServicioDetalle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

@WebMvcTest(ControladorDetalle.class)
public class ControladorDetalleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioDetalle servicioDetalle;

    @Test
    public void listarPorFactura_DebeRetornar200() throws Exception {
        when(servicioDetalle.listarPorFactura(1L)).thenReturn(List.of());
        mockMvc.perform(get("/api/detalles/factura/1")).andExpect(status().isOk());
    }

    @Test
    public void obtenerPorId_DebeRetornar200() throws Exception {
        DtoDetalle dto = mock(DtoDetalle.class);
        when(servicioDetalle.obtenerPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/detalles/1")).andExpect(status().isOk());
    }

    @Test
    public void eliminar_DebeRetornar204() throws Exception {
        doNothing().when(servicioDetalle).eliminar(1L);
        mockMvc.perform(delete("/api/detalles/1")).andExpect(status().isNoContent());
    }
}