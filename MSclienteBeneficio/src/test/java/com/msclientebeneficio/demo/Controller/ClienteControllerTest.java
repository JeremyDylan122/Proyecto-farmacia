package com.msclientebeneficio.demo.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.msclientebeneficio.demo.Dto.ClienteDTO;
import com.msclientebeneficio.demo.Service.ClienteService;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
    }

    @Test
    public void obtenerClientePorRun_DeberiaRetornar200YCliente() throws Exception {
        // Given
        String run = "12345678";
        ClienteDTO dto = new ClienteDTO();
        dto.setRun(run);
        dto.setDv("9");
        dto.setNombre("Jeremy");

        when(clienteService.obtenerClientePorRun(run)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/clientes/{run}", run))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.run").value(run))
                .andExpect(jsonPath("$.nombre").value("Jeremy"));

        verify(clienteService).obtenerClientePorRun(run);
    }

    @Test
    public void crearCliente_DeberiaRetornar201YCliente() throws Exception {
        // Given
        String run = "12345678";
        ClienteDTO dtoOutput = new ClienteDTO();
        dtoOutput.setRun(run);
        dtoOutput.setNombre("Jeremy");

        when(clienteService.crearCliente(any(ClienteDTO.class))).thenReturn(dtoOutput);

        String jsonRequest = "{"
                + "\"run\":\"12345678\","
                + "\"dv\":\"9\","
                + "\"nombre\":\"Jeremy\","
                + "\"apellido\":\"LastName\","
                + "\"correo\":\"jeremy@test.com\","
                + "\"telefono\":\"987654321\""
                + "}";

        // When & Then
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.run").value(run))
                .andExpect(jsonPath("$.nombre").value("Jeremy"));

        verify(clienteService).crearCliente(any(ClienteDTO.class));
    }

    @Test
    public void actualizarCliente_DeberiaRetornar200YCliente() throws Exception {
        // Given
        String run = "12345678";
        ClienteDTO dtoOutput = new ClienteDTO();
        dtoOutput.setRun(run);
        dtoOutput.setNombre("Jeremy");

        when(clienteService.actualizarCliente(eq(run), any(ClienteDTO.class))).thenReturn(dtoOutput);

        String jsonRequest = "{"
                + "\"run\":\"12345678\","
                + "\"dv\":\"9\","
                + "\"nombre\":\"Jeremy\","
                + "\"apellido\":\"LastName\","
                + "\"correo\":\"jeremy@test.com\","
                + "\"telefono\":\"987654321\""
                + "}";

        // When & Then
        mockMvc.perform(put("/api/clientes/{run}", run)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.run").value(run));

        verify(clienteService).actualizarCliente(eq(run), any(ClienteDTO.class));
    }

    @Test
    public void eliminarCliente_DeberiaRetornar24NoContent() throws Exception {
        // Given
        String run = "12345678";
        String dv = "9";
        when(clienteService.eliminarCliente(run, dv)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/clientes/{run}/{dv}", run, dv))
                .andExpect(status().isNoContent());

        verify(clienteService).eliminarCliente(run, dv);
    }
}
