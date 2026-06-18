package com.boleta.gestionboleta.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import com.boleta.gestionboleta.dto.RecetaClienteRequestDTO;
import com.boleta.gestionboleta.dto.RecetaClienteResponseDTO;
import com.boleta.gestionboleta.service.RecetaClienteService;

@ExtendWith(MockitoExtension.class)
public class RecetaClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecetaClienteService recetaClienteService;

    @InjectMocks
    private RecetaClienteController recetaClienteController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(recetaClienteController).build();
    }

    @Test
    public void registrarReceta_DeberiaRetornar201YReceta() throws Exception {
        // Given
        RecetaClienteResponseDTO response = new RecetaClienteResponseDTO();
        response.setId(1L);
        response.setRunCliente("12345678");
        response.setTipoReceta("Retenoide");
        response.setFolioReceta("F123");
        response.setActiva(true);

        when(recetaClienteService.registrarReceta(any(RecetaClienteRequestDTO.class))).thenReturn(response);

        String jsonRequest = "{"
                + "\"runCliente\":\"12345678\","
                + "\"tipoReceta\":\"Retenoide\","
                + "\"folioReceta\":\"F123\","
                + "\"fechaEmision\":\"2026-06-07\","
                + "\"fechaVencimiento\":\"2026-07-07\""
                + "}";

        // When & Then
        mockMvc.perform(post("/api/recetas-clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.runCliente").value("12345678"))
                .andExpect(jsonPath("$.folioReceta").value("F123"))
                .andExpect(jsonPath("$.activa").value(true));

        verify(recetaClienteService).registrarReceta(any(RecetaClienteRequestDTO.class));
    }

    @Test
    public void obtenerPorId_DeberiaRetornar200YReceta() throws Exception {
        // Given
        Long id = 1L;
        RecetaClienteResponseDTO response = new RecetaClienteResponseDTO();
        response.setId(id);
        response.setRunCliente("12345678");
        response.setFolioReceta("F123");

        when(recetaClienteService.obtenerPorId(id)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/recetas-clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.runCliente").value("12345678"));

        verify(recetaClienteService).obtenerPorId(id);
    }

    @Test
    public void listarPorRunCliente_DeberiaRetornar200YLista() throws Exception {
        // Given
        String run = "12345678";
        RecetaClienteResponseDTO response = new RecetaClienteResponseDTO();
        response.setId(1L);
        response.setRunCliente(run);

        when(recetaClienteService.listarPorRunCliente(run)).thenReturn(Collections.singletonList(response));

        // When & Then
        mockMvc.perform(get("/api/recetas-clientes/cliente/{run}", run))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].runCliente").value(run));

        verify(recetaClienteService).listarPorRunCliente(run);
    }

    @Test
    public void desactivarReceta_DeberiaRetornar24NoContent() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(recetaClienteService).desactivarReceta(id);

        // When & Then
        mockMvc.perform(patch("/api/recetas-clientes/{id}/desactivar", id))
                .andExpect(status().isNoContent());

        verify(recetaClienteService).desactivarReceta(id);
    }
}
