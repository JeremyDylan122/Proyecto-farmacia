package com.compra.farma.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.compra.farma.dto.DtoCompra;
import com.compra.farma.service.ServicioCompra;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

@WebMvcTest(ControladorCompra.class)
public class ControladorCompraTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioCompra servicioCompra;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void crear_DebeRetornar201() throws Exception {
        DtoCompra dtoMock = mock(DtoCompra.class);
        when(servicioCompra.crear(any(DtoCompra.class))).thenReturn(dtoMock);

        // Usamos un mapa para estructurar los datos sin usar 'set'
        // De esta forma, cambiamos el LOTE por un número puro (2026) directamente.
        Map<String, Object> compraMap = Map.of(
            "idOrdenCompra", 1L,
            "rutProveedor", "12345678-9",
            "sku", 100L,
            "cantidad", 5,
            "totalCompra", 15000.0,
            "codigoLote", 2026L, 
            "fechaVencimiento", "2026-12-31"
        );

        mockMvc.perform(post("/api/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compraMap))) // Convierte el mapa a un JSON perfecto
                .andExpect(status().isCreated());
    }

    @Test
    public void listar_DebeRetornar200() throws Exception {
        when(servicioCompra.listaCompras()).thenReturn(List.of());

        mockMvc.perform(get("/api/compras"))
                .andExpect(status().isOk());
    }

    @Test
    public void obtenerPorId_DebeRetornar200() throws Exception {
        DtoCompra dto = mock(DtoCompra.class);
        when(servicioCompra.buscarPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/compras/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void eliminarCompra_DebeRetornar204() throws Exception {
        doNothing().when(servicioCompra).eliminarCompra(1L);

        mockMvc.perform(delete("/api/compras/1"))
                .andExpect(status().isNoContent());
    }
}