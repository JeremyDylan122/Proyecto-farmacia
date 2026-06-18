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

import com.inventario.msinventario.dto.stockDTOs.StockDTO;
import com.inventario.msinventario.exceptions.RecursoNoEncontradoException;
import com.inventario.msinventario.service.StockService;

@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

    @Test
    void whenListarStock_thenReturnOk() throws Exception {
        // GIVEN
        List<StockDTO> listaStock = List.of(new StockDTO());

        // WHEN
        when(stockService.listarStock()).thenReturn(listaStock);

        // THEN
        mockMvc.perform(get("/api/v1/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenListarStock_throwInternalServerError() throws Exception {
        // GIVEN
        when(stockService.listarStock()).thenThrow(new RuntimeException("Error al consultar el stock"));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/stock"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenBuscarPorSku_thenReturnOk() throws Exception {
        // GIVEN
        Long skuExistente = 1L; 
        StockDTO stockDTO = new StockDTO();
        
        stockDTO.setSku(skuExistente); 

        // WHEN
        when(stockService.buscarPorSku(skuExistente)).thenReturn(stockDTO);

        // THEN
        mockMvc.perform(get("/api/v1/stock/{sku}", skuExistente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(skuExistente));
    }

    @Test
    void whenBuscarPorSku_thenNotFound() throws Exception {
        // GIVEN
        Long skuNoExistente = 99L;

        // WHEN
        when(stockService.buscarPorSku(skuNoExistente))
                .thenThrow(new RecursoNoEncontradoException("Stock no encontrado para el SKU especificado"));

        // THEN
        mockMvc.perform(get("/api/v1/stock/{sku}", skuNoExistente))
                .andExpect(status().isNotFound()); 
    }

}
