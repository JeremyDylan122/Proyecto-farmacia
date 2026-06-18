package com.compra.farma.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.compra.farma.dto.DetalleMapper;
import com.compra.farma.dto.DtoDetalle;
import com.compra.farma.model.ModeloDetalleFactura;
import com.compra.farma.repository.DetalleFacturaRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ServicioDetalleTest {

    @Mock
    private DetalleFacturaRepositorio repoDetalle;

    @Mock
    private DetalleMapper mapper;

    @InjectMocks
    private ServicioDetalle servicioDetalle;

    @Test
    void listarPorFactura_DebeRetornarLista() {
        ModeloDetalleFactura detalle = new ModeloDetalleFactura();
        DtoDetalle dto = mock(DtoDetalle.class);

        when(repoDetalle.findByFactura_IdFactura(1L)).thenReturn(List.of(detalle));
        when(mapper.toDTO(detalle)).thenReturn(dto);

        List<DtoDetalle> resultado = servicioDetalle.listarPorFactura(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_CuandoExiste_DebeRetornarDto() {
        ModeloDetalleFactura detalle = new ModeloDetalleFactura();
        DtoDetalle dto = mock(DtoDetalle.class);

        when(repoDetalle.findById(1L)).thenReturn(Optional.of(detalle));
        when(mapper.toDTO(detalle)).thenReturn(dto);

        DtoDetalle resultado = servicioDetalle.obtenerPorId(1L);

        assertNotNull(resultado);
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DebeLanzarExcepcion() {
        when(repoDetalle.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> servicioDetalle.obtenerPorId(1L));
        assertTrue(ex.getMessage().contains("Detalle no encontrado"));
    }

    @Test
    void eliminar_CuandoExiste_DebeEliminar() {
        when(repoDetalle.existsById(1L)).thenReturn(true);
        doNothing().when(repoDetalle).deleteById(1L);

        assertDoesNotThrow(() -> servicioDetalle.eliminar(1L));
        verify(repoDetalle, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_CuandoNoExiste_DebeLanzarExcepcion() {
        when(repoDetalle.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> servicioDetalle.eliminar(1L));
        assertTrue(ex.getMessage().contains("Detalle no encontrado"));
    }
}