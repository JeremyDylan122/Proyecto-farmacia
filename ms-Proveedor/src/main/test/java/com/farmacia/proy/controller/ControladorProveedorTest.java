package com.farmacia.proy.controller; 

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.farmacia.proy.dto.ProveedorRequestDto;
import com.farmacia.proy.dto.ProveedorResponseDto;
import com.farmacia.proy.service.ServicioProveedor;

// Si tu clase ControladorProveedor principal está en otro paquete, añade su import aquí. Ej:
// import com.farmacia.proy.controlador.ControladorProveedor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

@WebMvcTest(ControladorProveedor.class) 
public class ControladorProveedorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioProveedor servicioProveedor;

    @Test
    public void crear_DebeRetornar201() throws Exception {
        ProveedorResponseDto dtoMock = mock(ProveedorResponseDto.class);
        when(servicioProveedor.crearProveedor(any(ProveedorRequestDto.class))).thenReturn(dtoMock);

        String jsonProveedor = "{\"rutProveedor\":\"12345678-9\",\"nombre\":\"Proveedor Test\",\"email\":\"test@farmacia.com\",\"telefono\":\"987654321\"}";

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonProveedor))
                .andExpect(status().isCreated());
    }

    @Test
    public void obtenerTodos_DebeRetornar200() throws Exception {
        when(servicioProveedor.obtenerProveedores()).thenReturn(List.of());

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk());
    }

    @Test
    public void buscarPorRut_DebeRetornar200() throws Exception {
        ProveedorResponseDto dtoMock = mock(ProveedorResponseDto.class);
        when(servicioProveedor.buscarPorRut("12345678-9")).thenReturn(dtoMock);

        mockMvc.perform(get("/api/proveedores/12345678-9"))
                .andExpect(status().isOk());
    }

    @Test
    public void eliminarPorRut_DebeRetornar204() throws Exception {
        doNothing().when(servicioProveedor).eliminarProveedor("12345678-9");

        mockMvc.perform(delete("/api/proveedores/12345678-9"))
                .andExpect(status().isNoContent());
    }
}