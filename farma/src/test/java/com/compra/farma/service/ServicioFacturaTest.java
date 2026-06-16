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

import com.compra.farma.dto.DetalleMapper;
import com.compra.farma.dto.DtoDetalle;
import com.compra.farma.dto.DtoFactura;
import com.compra.farma.dto.FacturaMapper;
import com.compra.farma.exception.CompraNoEncontradaException;
import com.compra.farma.model.ModeloCompra;
import com.compra.farma.model.ModeloDetalleFactura;
import com.compra.farma.model.ModeloFactura;
import com.compra.farma.repository.ModeloFacturaRepositorio;
import com.compra.farma.repository.RepositorioCompra;

@ExtendWith(MockitoExtension.class)
public class ServicioFacturaTest {

    @Mock
    private ModeloFacturaRepositorio facturaRepo;
    @Mock
    private RepositorioCompra compraRepo;
    @Mock
    private DetalleMapper detalleMapper;
    @Mock
    private FacturaMapper facturaMapper;

    @InjectMocks
    private ServicioFactura servicioFactura;

    private DtoFactura dtoFactura;
    private ModeloFactura modeloFactura;
    private ModeloCompra modeloCompra;

    @BeforeEach
    void setUp() {
        modeloFactura = new ModeloFactura();
        modeloCompra = new ModeloCompra();
        dtoFactura = mock(DtoFactura.class);
    }

    @Test
    void cuandoCrearFacturaOk_entoncesRetornaFacturaGuardada() {
        DtoDetalle detDto = mock(DtoDetalle.class);
        when(detDto.getIdOrdenCompra()).thenReturn(1L);
        
        when(dtoFactura.getDetalles()).thenReturn(List.of(detDto));

        ModeloDetalleFactura detalleEntity = new ModeloDetalleFactura();

        when(facturaMapper.toEntity(any(DtoFactura.class))).thenReturn(modeloFactura);
        when(detalleMapper.toEntity(any(DtoDetalle.class))).thenReturn(detalleEntity);
        when(compraRepo.findById(1L)).thenReturn(Optional.of(modeloCompra));
        when(facturaRepo.save(any(ModeloFactura.class))).thenReturn(modeloFactura);
        when(facturaMapper.toDTO(any(ModeloFactura.class))).thenReturn(dtoFactura);

        DtoFactura resultado = servicioFactura.crearFactura(dtoFactura);

        assertNotNull(resultado);
        verify(facturaRepo, times(1)).save(any(ModeloFactura.class));
        verify(compraRepo, times(1)).findById(1L);
    }

    @Test
    void cuandoCrearFacturaConCompraInexistente_entoncesLanzaExcepcion() {
        DtoDetalle detDto = mock(DtoDetalle.class);
        when(detDto.getIdOrdenCompra()).thenReturn(99L);
        
        when(dtoFactura.getDetalles()).thenReturn(List.of(detDto));

        when(facturaMapper.toEntity(any(DtoFactura.class))).thenReturn(modeloFactura);
        when(detalleMapper.toEntity(any(DtoDetalle.class))).thenReturn(new ModeloDetalleFactura());
        
        when(compraRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CompraNoEncontradaException.class, () -> {
            servicioFactura.crearFactura(dtoFactura);
        });

        verify(facturaRepo, never()).save(any(ModeloFactura.class));
    }

    @Test
    void cuandoListarTodos_entoncesRetornaListaFacturas() {
        // Simulamos que la base de datos devuelve una lista con una factura
        when(facturaRepo.findAll()).thenReturn(List.of(modeloFactura));
        when(facturaMapper.toDTO(any(ModeloFactura.class))).thenReturn(dtoFactura);

        List<DtoFactura> resultado = servicioFactura.listarFacturas();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(facturaRepo, times(1)).findAll();
    }
}