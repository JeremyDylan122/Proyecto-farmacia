package com.boleta.gestionboleta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.boleta.gestionboleta.client.ClienteBeneficioClient;
import com.boleta.gestionboleta.client.InventarioClient;
import com.boleta.gestionboleta.client.dto.ClienteRemotoDTO;
import com.boleta.gestionboleta.client.dto.ProductoRemotoDTO;
import com.boleta.gestionboleta.dto.ActualizarBoletaProductosRequestDTO;
import com.boleta.gestionboleta.dto.BoletaProductoRequestDTO;
import com.boleta.gestionboleta.dto.BoletaResponseDTO;
import com.boleta.gestionboleta.dto.CrearBoletaRequestDTO;
import com.boleta.gestionboleta.excepcions.RecursoNoEncontradoException;
import com.boleta.gestionboleta.excepcions.ReglaNegocioException;
import com.boleta.gestionboleta.model.Boleta;
import com.boleta.gestionboleta.model.ClienteSnapshot;
import com.boleta.gestionboleta.repository.BoletaRepository;
import com.boleta.gestionboleta.repository.RecetaClienteRepository;

@ExtendWith(MockitoExtension.class)
public class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private RecetaClienteRepository recetaClienteRepository;

    @Mock
    private ClienteBeneficioClient clienteBeneficioClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private BoletaService boletaService;

    @Test
    public void crearBoleta_ConDatosValidos_DeberiaCalcularYGuardarBoleta() {
        // Given
        String run = "12345678";
        Long sku = 1001L;

        CrearBoletaRequestDTO request = new CrearBoletaRequestDTO();
        request.setRunCliente(run);
        
        BoletaProductoRequestDTO prodReq = new BoletaProductoRequestDTO();
        prodReq.setSku(sku);
        prodReq.setCantidad(2);
        request.setProductos(Collections.singletonList(prodReq));

        ClienteRemotoDTO clienteRemoto = new ClienteRemotoDTO();
        clienteRemoto.setRun(run);
        clienteRemoto.setDv("9");
        clienteRemoto.setNombre("Jeremy");
        clienteRemoto.setApellido("LastName");
        clienteRemoto.setCorreo("jeremy@test.com");
        clienteRemoto.setIdBeneficio(1L);

        ProductoRemotoDTO productoRemoto = new ProductoRemotoDTO();
        productoRemoto.setSku(sku);
        productoRemoto.setNombre("Paracetamol");
        productoRemoto.setPrecio(BigDecimal.valueOf(10000));
        productoRemoto.setActivo(true);
        productoRemoto.setTipoReceta("venta libre");

        when(clienteBeneficioClient.obtenerClientePorRun(run)).thenReturn(clienteRemoto);
        when(clienteBeneficioClient.obtenerDescuentoPorId(1L)).thenReturn(15); // 15% descuento
        when(inventarioClient.obtenerProductoPorSku(sku)).thenReturn(productoRemoto);
        when(boletaRepository.findMaxFolio()).thenReturn(100L);

        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> {
            Boleta saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // When
        BoletaResponseDTO response = boletaService.crearBoleta(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(101L, response.getFolio()); // MaxFolio + 1
        // Subtotal = 10000 * 2 = 20000. Descuento = 15% -> 3000. Neto = 17000. IVA = 19% -> 3230. Bruto = 20230.
        assertEquals(BigDecimal.valueOf(20230).setScale(2), response.getMontoBruto());
        assertEquals(BigDecimal.valueOf(3000).setScale(2), response.getMontoDescuento());
        verify(boletaRepository).save(any(Boleta.class));
    }

    @Test
    public void crearBoleta_ConCantidadMayorADiez_DeberiaLanzarReglaNegocioException() {
        // Given
        String run = "12345678";
        Long sku = 1001L;

        CrearBoletaRequestDTO request = new CrearBoletaRequestDTO();
        request.setRunCliente(run);
        
        BoletaProductoRequestDTO prodReq = new BoletaProductoRequestDTO();
        prodReq.setSku(sku);
        prodReq.setCantidad(11);
        request.setProductos(Collections.singletonList(prodReq));

        ClienteRemotoDTO clienteRemoto = new ClienteRemotoDTO();
        clienteRemoto.setRun(run);
        clienteRemoto.setIdBeneficio(null);

        when(clienteBeneficioClient.obtenerClientePorRun(run)).thenReturn(clienteRemoto);

        // When & Then
        assertThrows(ReglaNegocioException.class, () -> {
            boletaService.crearBoleta(request);
        });
        verify(boletaRepository, never()).save(any(Boleta.class));
    }

    @Test
    public void crearBoleta_SinRecetaVigente_DeberiaLanzarReglaNegocioException() {
        // Given
        String run = "12345678";
        Long sku = 1001L;

        CrearBoletaRequestDTO request = new CrearBoletaRequestDTO();
        request.setRunCliente(run);
        
        BoletaProductoRequestDTO prodReq = new BoletaProductoRequestDTO();
        prodReq.setSku(sku);
        prodReq.setCantidad(2);
        request.setProductos(Collections.singletonList(prodReq));

        ClienteRemotoDTO clienteRemoto = new ClienteRemotoDTO();
        clienteRemoto.setRun(run);
        clienteRemoto.setIdBeneficio(null);

        ProductoRemotoDTO productoRemoto = new ProductoRemotoDTO();
        productoRemoto.setSku(sku);
        productoRemoto.setNombre("Clonazepam");
        productoRemoto.setPrecio(BigDecimal.valueOf(5000));
        productoRemoto.setActivo(true);
        productoRemoto.setTipoReceta("Retenida");

        when(clienteBeneficioClient.obtenerClientePorRun(run)).thenReturn(clienteRemoto);
        when(inventarioClient.obtenerProductoPorSku(sku)).thenReturn(productoRemoto);
        when(recetaClienteRepository.existsRecetaVigente(run, "Retenida", LocalDate.now())).thenReturn(false);

        // When & Then
        assertThrows(ReglaNegocioException.class, () -> {
            boletaService.crearBoleta(request);
        });
        verify(boletaRepository, never()).save(any(Boleta.class));
    }

    @Test
    public void buscarPorId_Existente_DeberiaRetornarBoleta() {
        // Given
        Long id = 1L;
        Boleta boleta = new Boleta();
        boleta.setId(id);
        boleta.setFolio(101L);
        ClienteSnapshot cliente = new ClienteSnapshot();
        cliente.setRun("12345678");
        boleta.setCliente(cliente);
        boleta.setMontoBruto(BigDecimal.valueOf(20000));

        when(boletaRepository.findBoletaCompletaById(id)).thenReturn(Optional.of(boleta));

        // When
        BoletaResponseDTO response = boletaService.buscarPorId(id);

        // Then
        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    @Test
    public void buscarPorId_NoExistente_DeberiaLanzarRecursoNoEncontradoException() {
        // Given
        Long id = 99L;
        when(boletaRepository.findBoletaCompletaById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            boletaService.buscarPorId(id);
        });
    }

    @Test
    public void actualizarProductos_Exitoso_DeberiaRecalcularYGuardar() {
        // Given
        Long id = 1L;
        String run = "12345678";
        Long sku = 1001L;

        Boleta boleta = new Boleta();
        boleta.setId(id);
        boleta.setFolio(101L);
        boleta.setAnulada(false);
        ClienteSnapshot cliente = new ClienteSnapshot();
        cliente.setRun(run);
        cliente.setIdBeneficio(1L);
        boleta.setCliente(cliente);

        ActualizarBoletaProductosRequestDTO request = new ActualizarBoletaProductosRequestDTO();
        BoletaProductoRequestDTO prodReq = new BoletaProductoRequestDTO();
        prodReq.setSku(sku);
        prodReq.setCantidad(3);
        request.setProductos(Collections.singletonList(prodReq));

        ClienteRemotoDTO clienteRemoto = new ClienteRemotoDTO();
        clienteRemoto.setRun(run);
        clienteRemoto.setIdBeneficio(1L);

        ProductoRemotoDTO productoRemoto = new ProductoRemotoDTO();
        productoRemoto.setSku(sku);
        productoRemoto.setNombre("Paracetamol");
        productoRemoto.setPrecio(BigDecimal.valueOf(10000));
        productoRemoto.setActivo(true);
        productoRemoto.setTipoReceta("venta libre");

        when(boletaRepository.findBoletaCompletaById(id)).thenReturn(Optional.of(boleta));
        when(clienteBeneficioClient.obtenerClientePorRun(run)).thenReturn(clienteRemoto);
        when(clienteBeneficioClient.obtenerDescuentoPorId(1L)).thenReturn(15);
        when(inventarioClient.obtenerProductoPorSku(sku)).thenReturn(productoRemoto);
        when(boletaRepository.save(any(Boleta.class))).thenReturn(boleta);

        // When
        BoletaResponseDTO response = boletaService.actualizarProductos(id, request);

        // Then
        assertNotNull(response);
        assertEquals(id, response.getId());
        verify(boletaRepository).save(boleta);
    }

    @Test
    public void anularBoleta_Exitoso_DeberiaMarcarComoAnulada() {
        // Given
        Long id = 1L;
        Boleta boleta = new Boleta();
        boleta.setId(id);
        boleta.setFolio(101L);
        boleta.setAnulada(false);

        when(boletaRepository.findBoletaCompletaById(id)).thenReturn(Optional.of(boleta));
        when(boletaRepository.save(any(Boleta.class))).thenReturn(boleta);

        // When
        boletaService.anularBoleta(id);

        // Then
        assertTrue(boleta.isAnulada());
        assertNotNull(boleta.getFechaAnulacion());
        verify(boletaRepository).save(boleta);
    }

    @Test
    public void anularBoleta_YaAnulada_DeberiaLanzarReglaNegocioException() {
        // Given
        Long id = 1L;
        Boleta boleta = new Boleta();
        boleta.setId(id);
        boleta.setAnulada(true);

        when(boletaRepository.findBoletaCompletaById(id)).thenReturn(Optional.of(boleta));

        // When & Then
        assertThrows(ReglaNegocioException.class, () -> {
            boletaService.anularBoleta(id);
        });
        verify(boletaRepository, never()).save(any(Boleta.class));
    }
}
