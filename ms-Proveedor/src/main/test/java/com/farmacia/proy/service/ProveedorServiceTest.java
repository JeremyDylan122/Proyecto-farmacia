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
import reactor.core.publisher.Mono;

import java.util.List;
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
    void crearProveedor_EmailYaExiste_DebeLanzarException() {
        ProveedorRequestDto requestDto = new ProveedorRequestDto();
        requestDto.setRutProveedor("12345678-9");
        requestDto.setEmail("duplicado@correo.com");
        
        when(repositorio.existsById("12345678-9")).thenReturn(false);
        when(repositorio.existsByEmail("duplicado@correo.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            servicio.crearProveedor(requestDto);
        });

        assertEquals("Ya existe proveedor con este email: duplicado@correo.com", exception.getMessage());
    }

    @Test
    void obtenerProveedores_DebeRetornarLista() {
        Proveedor proveedorFalso = new Proveedor();
        proveedorFalso.setRutProveedor("11111111-1");
        when(repositorio.findAll()).thenReturn(List.of(proveedorFalso));

        List<ProveedorResponseDto> resultado = servicio.obtenerProveedores();
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
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

    @Test
    void verificarMedicamentoEnFarma_CuandoEsExitoso_DebeRetornarTrue() {
        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        boolean resultado = servicio.verificarMedicamentoEnFarma(1L);
        assertTrue(resultado);
    }

    @Test
    void verificarMedicamentoEnFarma_CuandoFallaWebClient_DebeRetornarFalse() {
        WebClient webClientMock = mock(WebClient.class);
        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.get()).thenThrow(new RuntimeException("Error de conexión"));

        boolean resultado = servicio.verificarMedicamentoEnFarma(1L);
        assertFalse(resultado);
    }

    @Test
    void enviarCompraACompras_CuandoEsExitoso_DebeRetornarObjeto() {
        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Object.class)).thenReturn(Mono.just(new Object()));

        Object resultado = servicio.enviarCompraACompras(new Object());
        assertNotNull(resultado);
    }

    @Test
    void enviarCompraACompras_CuandoFallaWebClient_DebeLanzarException() {
        WebClient webClientMock = mock(WebClient.class);
        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.post()).thenThrow(new RuntimeException("Error de conexión"));

        assertThrows(RuntimeException.class, () -> {
            servicio.enviarCompraACompras(new Object());
        });
    }
}