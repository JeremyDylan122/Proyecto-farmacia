package com.msclientebeneficio.demo.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.msclientebeneficio.demo.Dto.BeneficioDTO;
import com.msclientebeneficio.demo.Dto.BeneficioDTOMapper;
import com.msclientebeneficio.demo.Model.Beneficio;
import com.msclientebeneficio.demo.Service.BeneficioService;

@ExtendWith(MockitoExtension.class)
public class BeneficioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BeneficioService beneficioService;

    @Mock
    private BeneficioDTOMapper beneficioDTOMapper;

    @InjectMocks
    private BeneficioController beneficioController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(beneficioController).build();
    }

    @Test
    public void obtenerBeneficioPorId_DeberiaRetornar200YBeneficio() throws Exception {
        // Given
        Long id = 1L;
        Beneficio beneficio = new Beneficio();
        beneficio.setId(id);

        BeneficioDTO dto = new BeneficioDTO();
        dto.setId(id);
        dto.setNombre("Fonasa");
        dto.setDescuento(15);

        when(beneficioService.findById(id)).thenReturn(beneficio);
        when(beneficioDTOMapper.toDTO(beneficio)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/beneficios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Fonasa"))
                .andExpect(jsonPath("$.descuento").value(15));

        verify(beneficioService).findById(id);
        verify(beneficioDTOMapper).toDTO(beneficio);
    }

    @Test
    public void obtenerDescuentoPorId_DeberiaRetornar200YDescuento() throws Exception {
        // Given
        Long id = 1L;
        when(beneficioService.obtenerDescuentoPorId(id)).thenReturn(15);

        // When & Then
        mockMvc.perform(get("/api/beneficios/{id}/descuento", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(15));

        verify(beneficioService).obtenerDescuentoPorId(id);
    }
}
