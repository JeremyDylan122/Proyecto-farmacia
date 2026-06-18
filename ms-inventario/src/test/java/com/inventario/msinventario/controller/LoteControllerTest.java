package com.inventario.msinventario.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.inventario.msinventario.dto.loteDTOs.LoteDTO;
import com.inventario.msinventario.exceptions.RecursoNoEncontradoException;
import com.inventario.msinventario.service.LoteService;

@WebMvcTest(LoteController.class)
public class LoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoteService loteService;

    @Test
    void whenListarTodos_thenReturnOk() throws Exception {
        // GIVEN
        List<LoteDTO> listaLotes = List.of(new LoteDTO()); 

        // WHEN
        when(loteService.listarTodos()).thenReturn(listaLotes);

        // THEN
        mockMvc.perform(get("/api/v1/lotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenListarTodos_throwInternalServerError() throws Exception {
        // GIVEN
        when(loteService.listarTodos()).thenThrow(new RuntimeException("Error en el servidor al traer lotes"));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/lotes"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenBuscarPorId_thenReturnOk() throws Exception {
        // GIVEN
        String codigoLoteExistente = "LOTE-2026";
        LoteDTO loteDTO = new LoteDTO();
        loteDTO.setCodigoLote(codigoLoteExistente);

        // WHEN
        when(loteService.buscarPorId(codigoLoteExistente)).thenReturn(loteDTO);

        // THEN
        mockMvc.perform(get("/api/v1/lotes/{codigoLote}", codigoLoteExistente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoLote").value(codigoLoteExistente));
    }

    @Test
    void whenBuscarPorId_thenNotFound() throws Exception {
        // GIVEN
        String codigoLoteNoExistente = "LOTE-INEXISTENTE";

        // WHEN
        when(loteService.buscarPorId(codigoLoteNoExistente))
                .thenThrow(new RecursoNoEncontradoException("El lote solicitado no existe"));

        // THEN
        mockMvc.perform(get("/api/v1/lotes/{codigoLote}", codigoLoteNoExistente))
                .andExpect(status().isNotFound());
    }


}
