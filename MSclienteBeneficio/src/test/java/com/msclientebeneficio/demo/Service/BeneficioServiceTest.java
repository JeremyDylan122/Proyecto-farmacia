package com.msclientebeneficio.demo.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.msclientebeneficio.demo.Exception.RecursoNoEncontradoException;
import com.msclientebeneficio.demo.Model.Beneficio;
import com.msclientebeneficio.demo.Repository.BeneficioRepository;

@ExtendWith(MockitoExtension.class)
public class BeneficioServiceTest {

    @Mock
    private BeneficioRepository beneficioRepository;

    @InjectMocks
    private BeneficioService beneficioService;

    @Test
    public void findById_ConIdValidoExistente_DeberiaRetornarBeneficio() {
        // Given
        Long id = 1L;
        Beneficio beneficio = new Beneficio();
        beneficio.setId(id);
        beneficio.setNombre("Fonasa");
        beneficio.setDescuento(15);

        when(beneficioRepository.findById(id)).thenReturn(Optional.of(beneficio));

        // When
        Beneficio resultado = beneficioService.findById(id);

        // Then
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Fonasa", resultado.getNombre());
        verify(beneficioRepository).findById(id);
    }

    @Test
    public void findById_ConIdInexistente_DeberiaLanzarRecursoNoEncontradoException() {
        // Given
        Long id = 99L;
        when(beneficioRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            beneficioService.findById(id);
        });
    }


    @Test
    public void obtenerDescuentoPorId_ConIdValido_DeberiaRetornarDescuento() {
        // Given
        Long id = 1L;
        Beneficio beneficio = new Beneficio();
        beneficio.setId(id);
        beneficio.setDescuento(15);

        when(beneficioRepository.findById(id)).thenReturn(Optional.of(beneficio));

        // When
        Integer descuento = beneficioService.obtenerDescuentoPorId(id);

        // Then
        assertEquals(15, descuento);
    }
}
