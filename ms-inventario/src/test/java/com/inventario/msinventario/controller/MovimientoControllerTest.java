package com.inventario.msinventario.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

import com.inventario.msinventario.dto.movimientoDTOs.MovimientoDTO;
import com.inventario.msinventario.exceptions.RecursoNoEncontradoException;
import com.inventario.msinventario.service.MovimientoService;

@WebMvcTest(MovimientoController.class)
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovimientoService movimientoService;

    @Test
    void whenListarTodos_thenReturnOk() throws Exception {

        // GIVEN
        List<MovimientoDTO> movimientos = List.of(new MovimientoDTO());

        // WHEN
        when(movimientoService.listarTodos()).thenReturn(movimientos);

        // THEN
        mockMvc.perform(get("/api/v1/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenListarTodos_throwInternalServerError() throws Exception {

        // GIVEN
        when(movimientoService.listarTodos()).thenThrow(new RuntimeException("Error interno en la base de datos"));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/movimientos"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenListarPorId_thenReturnOk() throws Exception {

        // GIVEN
        Long idMovimiento = 1L;
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setReferenciaId(idMovimiento);

        // WHEN
        when(movimientoService.listarPorId(idMovimiento)).thenReturn(movimientoDTO);

        // THEN
        mockMvc.perform(get("/api/v1/movimientos/{idMovimiento}", idMovimiento))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenciaId").value(idMovimiento));
    }

    @Test
    void whenListarPorId_thenNotFound() throws Exception {

        // GIVEN
        Long idNoExistente = 99L;

        // WHEN
        when(movimientoService.listarPorId(idNoExistente))
                .thenThrow(new RecursoNoEncontradoException("Movimiento no encontrado"));

        // THEN
        mockMvc.perform(get("/api/v1/movimientos/{idMovimiento}", idNoExistente))
                .andExpect(status().isNotFound());
    }

}