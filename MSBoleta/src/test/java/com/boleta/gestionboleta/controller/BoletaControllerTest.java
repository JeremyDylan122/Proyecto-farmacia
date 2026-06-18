package com.boleta.gestionboleta.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.boleta.gestionboleta.dto.ActualizarBoletaProductosRequestDTO;
import com.boleta.gestionboleta.dto.CrearBoletaRequestDTO;
import com.boleta.gestionboleta.dto.BoletaResponseDTO;
import com.boleta.gestionboleta.service.BoletaService;

@ExtendWith(MockitoExtension.class)
public class BoletaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BoletaService boletaService;

    @InjectMocks
    private BoletaController boletaController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(boletaController).build();
    }

    @Test
    public void crearBoleta_DeberiaRetornar201YBoleta() throws Exception {
        // Given
        BoletaResponseDTO response = new BoletaResponseDTO();
        response.setId(1L);
        response.setFolio(101L);
        response.setMontoBruto(BigDecimal.valueOf(20230));

        when(boletaService.crearBoleta(any(CrearBoletaRequestDTO.class))).thenReturn(response);

        String jsonRequest = "{"
                + "\"runCliente\":\"12345678\","
                + "\"productos\":["
                + "  {\"sku\":1001,\"cantidad\":2}"
                + "]"
                + "}";

        // When & Then
        mockMvc.perform(post("/api/boletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.folio").value(101L))
                .andExpect(jsonPath("$.montoBruto").value(20230));

        verify(boletaService).crearBoleta(any(CrearBoletaRequestDTO.class));
    }

    @Test
    public void buscarPorId_DeberiaRetornar200YBoleta() throws Exception {
        // Given
        Long id = 1L;
        BoletaResponseDTO response = new BoletaResponseDTO();
        response.setId(id);
        response.setFolio(101L);

        when(boletaService.buscarPorId(id)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/boletas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.folio").value(101L));

        verify(boletaService).buscarPorId(id);
    }

    @Test
    public void listarPorRunCliente_DeberiaRetornar200YLista() throws Exception {
        // Given
        String run = "12345678";
        BoletaResponseDTO response = new BoletaResponseDTO();
        response.setId(1L);
        response.setFolio(101L);

        when(boletaService.listarPorRunCliente(run)).thenReturn(Collections.singletonList(response));

        // When & Then
        mockMvc.perform(get("/api/boletas/cliente/{run}", run))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].folio").value(101L));

        verify(boletaService).listarPorRunCliente(run);
    }

    @Test
    public void listarPorSkuProducto_DeberiaRetornar200YLista() throws Exception {
        // Given
        Long sku = 1001L;
        BoletaResponseDTO response = new BoletaResponseDTO();
        response.setId(1L);
        response.setFolio(101L);

        when(boletaService.listarPorSkuProducto(sku)).thenReturn(Collections.singletonList(response));

        // When & Then
        mockMvc.perform(get("/api/boletas/producto/{sku}", sku))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].folio").value(101L));

        verify(boletaService).listarPorSkuProducto(sku);
    }

    @Test
    public void actualizarProductos_DeberiaRetornar200YBoletaActualizada() throws Exception {
        // Given
        Long id = 1L;
        BoletaResponseDTO response = new BoletaResponseDTO();
        response.setId(id);
        response.setFolio(101L);
        response.setMontoBruto(BigDecimal.valueOf(30000));

        when(boletaService.actualizarProductos(eq(id), any(ActualizarBoletaProductosRequestDTO.class))).thenReturn(response);

        String jsonRequest = "{"
                + "\"productos\":["
                + "  {\"sku\":1001,\"cantidad\":3}"
                + "]"
                + "}";

        // When & Then
        mockMvc.perform(put("/api/boletas/{id}/productos", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.montoBruto").value(30000));

        verify(boletaService).actualizarProductos(eq(id), any(ActualizarBoletaProductosRequestDTO.class));
    }

    @Test
    public void anularBoleta_DeberiaRetornar24NoContent() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(boletaService).anularBoleta(id);

        // When & Then
        mockMvc.perform(patch("/api/boletas/{id}/anular", id))
                .andExpect(status().isNoContent());

        verify(boletaService).anularBoleta(id);
    }
}
