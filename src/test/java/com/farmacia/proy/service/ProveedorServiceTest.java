package com.farmacia.proy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.farmacia.proy.Exceptions.ProveedorNotFoundException;
import com.farmacia.proy.dto.ProveedorRequestDto;
import com.farmacia.proy.dto.ProveedorResponseDto;
import com.farmacia.proy.model.Proveedor;
import com.farmacia.proy.repository.RepositorioProveedor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

public class ProveedorServiceTest {

    @Mock
    private RepositorioProveedor repositorio;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private ServicioProveedor servicio; 

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearProveedor_Exitoso_DebeRetornarProveedorCreado() {
        ProveedorRequestDto requestDto = new ProveedorRequestDto();
        requestDto.setRutProveedor("12345678-9");
        requestDto.setNombre("Farmacia de la pobla");
        requestDto.setTelefono("912315699");
        requestDto.setDireccion("Av. Siempre Viva 123");
        requestDto.setEmail("alfa@correo.com");
        
        when(repositorio.existsById("12345678-9")).thenReturn(false);
        when(repositorio.existsByEmail("alfa@correo.com")).thenReturn(false);
        
        Proveedor proveedorGuardado = new Proveedor();
        proveedorGuardado.setRutProveedor("12345678-9");
        proveedorGuardado.setNombre("Farmacia de la pobla");
        proveedorGuardado.setTelefono("912315699");
        proveedorGuardado.setDireccion("Av. Siempre Viva 123");
        proveedorGuardado.setEmail("alfa@correo.com");
        
        when(repositorio.save(any(Proveedor.class))).thenReturn(proveedorGuardado);

        ProveedorResponseDto respuesta = servicio.crearProveedor(requestDto);

        assertNotNull(respuesta);
        assertEquals("12345678-9", respuesta.getRutProveedor());
        assertEquals("Farmacia de la pobla", respuesta.getNombre());
        verify(repositorio, times(1)).save(any(Proveedor.class));
    }

    @Test
    void crearProveedor_RutYaExiste_DebeLanzarException() {
        ProveedorRequestDto requestDto = new ProveedorRequestDto();
        requestDto.setRutProveedor("12345678-9");
        
        when(repositorio.existsById("12345678-9")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            servicio.crearProveedor(requestDto);
        });

        assertEquals("Ya existe proveedor con este rut: 12345678-9", exception.getMessage());
        verify(repositorio, never()).save(any(Proveedor.class));
    }

    @Test
    void buscarPorRut_CuandoExiste_DebeRetornarProveedor() {
        Proveedor proveedorFalso = new Proveedor();
        proveedorFalso.setRutProveedor("11111111-1");
        proveedorFalso.setNombre("Laboratorio Chile");
        
        when(repositorio.findById("11111111-1")).thenReturn(Optional.of(proveedorFalso));

        ProveedorResponseDto respuesta = servicio.buscarPorRut("11111111-1");

        assertNotNull(respuesta);
        assertEquals("Laboratorio Chile", respuesta.getNombre());
    }

    @Test
    void buscarPorRut_CuandoNoExiste_DebeLanzarProveedorNotFoundException() {
        when(repositorio.findById("99999999-9")).thenReturn(Optional.empty());

        assertThrows(ProveedorNotFoundException.class, () -> {
            servicio.buscarPorRut("99999999-9");
        });
    }

    @Test
    void eliminarProveedor_CuandoExiste_DebeEliminarCorrectamente() {
        when(repositorio.existsById("12345678-9")).thenReturn(true);

        servicio.eliminarProveedor("12345678-9");

        verify(repositorio, times(1)).deleteById("12345678-9");
    }

    @Test
    void eliminarProveedor_CuandoNoExiste_DebeLanzarProveedorNotFoundException() {
        when(repositorio.existsById("99999999-9")).thenReturn(false);

        assertThrows(ProveedorNotFoundException.class, () -> {
            servicio.eliminarProveedor("99999999-9");
        });
        verify(repositorio, never()).deleteById(anyString());
    }
}