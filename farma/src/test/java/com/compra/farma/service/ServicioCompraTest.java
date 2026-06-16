package com.compra.farma.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.compra.farma.ProveedorClient;
import com.compra.farma.dto.CompraMapper;
import com.compra.farma.dto.DtoCompra;
import com.compra.farma.exception.CompraNoEncontradaException;
import com.compra.farma.model.ModeloCompra;
import com.compra.farma.repository.RepositorioCompra;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ServicioCompraTest {

    @Mock
    private RepositorioCompra repo;
    @Mock
    private CompraMapper mapper;
    @Mock
    private ProveedorClient proveedorClient;
    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private ServicioCompra servicioCompra;

    private ModeloCompra modeloCompra;
    private DtoCompra dtoCompra;

    @BeforeEach
    void setUp() {
        modeloCompra = new ModeloCompra();
        dtoCompra = mock(DtoCompra.class);
    }

    @Test
    void cuandoCrearCompraOk_entoncesRetornaCompraGuardada() {
        when(mapper.toEntity(any(DtoCompra.class))).thenReturn(modeloCompra);
        when(repo.save(any(ModeloCompra.class))).thenReturn(modeloCompra);
        when(mapper.toDTO(any(ModeloCompra.class))).thenReturn(dtoCompra);

        DtoCompra resultado = servicioCompra.crear(dtoCompra);

        assertNotNull(resultado);
        verify(repo, times(1)).save(any(ModeloCompra.class));
    }

    @Test
    void cuandoBuscarPorIdExistente_entoncesRetornaCompraConProveedor() {
        Long compraId = 1L;
        String rutFalso = "12345678-9";
        
        modeloCompra.setRutProveedor(rutFalso);
        
        when(dtoCompra.idOrdenCompra()).thenReturn(compraId);
        when(dtoCompra.rutProveedor()).thenReturn(rutFalso);

        when(repo.findById(compraId)).thenReturn(Optional.of(modeloCompra));
        when(mapper.toDTO(modeloCompra)).thenReturn(dtoCompra);
        when(proveedorClient.obtenerProveedorPorRut(rutFalso)).thenReturn("Proveedor OK");

        DtoCompra resultado = servicioCompra.buscarPorId(compraId);

        assertNotNull(resultado);
        assertEquals(compraId, resultado.idOrdenCompra());
        verify(proveedorClient, times(1)).obtenerProveedorPorRut(rutFalso);
    }

    @Test
    void cuandoBuscarPorIdInexistente_entoncesLanzaExcepcion() {
        Long compraId = 99L;
        when(repo.findById(compraId)).thenReturn(Optional.empty());

        assertThrows(CompraNoEncontradaException.class, () -> {
            servicioCompra.buscarPorId(compraId);
        });
    }

    @Test
    void cuandoEnviarDatosALoteOk_entoncesRetornaRespuestaDelServicio() {
        Object datosEnviar = new Object();
        Object respuestaEsperada = new Object();

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        doReturn(webClientBuilder).when(webClientBuilder).baseUrl(any());
        doReturn(webClientMock).when(webClientBuilder).build();
        doReturn(requestBodyUriSpecMock).when(webClientMock).post();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestBodySpecMock).retrieve();
        
        when(responseSpecMock.bodyToMono(Object.class)).thenReturn(Mono.just(respuestaEsperada));

        Object resultado = servicioCompra.enviarDatosALote(datosEnviar);

        assertNotNull(resultado);
        verify(webClientBuilder, times(1)).build();
    }

    // ==========================================
    // ESCENARIOS AGREGADOS PARA COBERTURA AL 100%
    // ==========================================

    @Test
    void cuandoListaCompras_entoncesRetornaListaDeCompras() {
        List<ModeloCompra> listaFalsa = List.of(modeloCompra);
        when(repo.findAll()).thenReturn(listaFalsa);
        when(mapper.toDTO(modeloCompra)).thenReturn(dtoCompra);

        List<DtoCompra> resultado = servicioCompra.listaCompras();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void cuandoBuscarPorIdYProveedorFalla_entoncesRetornaMensajeDeErrorTemporal() {
        Long compraId = 1L;
        String rutFalso = "12345678-9";
        modeloCompra.setRutProveedor(rutFalso);
        
        when(dtoCompra.rutProveedor()).thenReturn(rutFalso);
        when(repo.findById(compraId)).thenReturn(Optional.of(modeloCompra));
        when(mapper.toDTO(modeloCompra)).thenReturn(dtoCompra);
        
        when(proveedorClient.obtenerProveedorPorRut(rutFalso)).thenThrow(new RuntimeException("Error de conexión"));

        DtoCompra resultado = servicioCompra.buscarPorId(compraId);

        assertNotNull(resultado);
        assertEquals("Información del proveedor no disponible temporalmente.", resultado.proveedor());
    }

    @Test
    void cuandoEliminarCompraExistente_entoncesEliminaCorrectamente() {
        Long compraId = 1L;
        when(repo.existsById(compraId)).thenReturn(true);
        doNothing().when(repo).deleteById(compraId);

        assertDoesNotThrow(() -> servicioCompra.eliminarCompra(compraId));
        verify(repo, times(1)).deleteById(compraId);
    }

    @Test
    void cuandoEnviarDatosALoteFalla_entoncesLanzaRuntimeException() {
        Object datosEnviar = new Object();

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        doReturn(webClientBuilder).when(webClientBuilder).baseUrl(any());
        doReturn(webClientMock).when(webClientBuilder).build();
        doReturn(requestBodyUriSpecMock).when(webClientMock).post();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestBodySpecMock).retrieve();
        
        when(responseSpecMock.bodyToMono(Object.class)).thenReturn(Mono.error(new RuntimeException("Timeout")));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            servicioCompra.enviarDatosALote(datosEnviar);
        });

        assertTrue(ex.getMessage().contains("No se pudo registrar el lote"));
    }
}