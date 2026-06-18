package com.boleta.gestionboleta.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.boleta.gestionboleta.dto.RecetaClienteRequestDTO;
import com.boleta.gestionboleta.dto.RecetaClienteResponseDTO;
import com.boleta.gestionboleta.excepcions.RecursoDuplicadoException;
import com.boleta.gestionboleta.excepcions.RecursoNoEncontradoException;
import com.boleta.gestionboleta.excepcions.RecursoNuloException;
import com.boleta.gestionboleta.excepcions.ReglaNegocioException;
import com.boleta.gestionboleta.model.RecetaCliente;
import com.boleta.gestionboleta.client.ClienteBeneficioClient;
import com.boleta.gestionboleta.repository.RecetaClienteRepository;

@ExtendWith(MockitoExtension.class)
public class RecetaClienteServiceTest {

    @Mock
    private RecetaClienteRepository recetaClienteRepository;

    @Mock
    private ClienteBeneficioClient clienteBeneficioClient;

    @InjectMocks
    private RecetaClienteService recetaClienteService;

    @Test
    public void registrarReceta_ConDatosValidos_DeberiaGuardarYRetornarReceta() {
        // Given
        RecetaClienteRequestDTO request = new RecetaClienteRequestDTO();
        request.setRunCliente("12345678");
        request.setTipoReceta("Retenida");
        request.setFolioReceta("F123");
        request.setFechaEmision(LocalDate.now());
        request.setFechaVencimiento(LocalDate.now().plusMonths(1));

        when(recetaClienteRepository.existsByRunClienteAndFolioReceta("12345678", "F123")).thenReturn(false);
        
        RecetaCliente recetaGuardada = new RecetaCliente();
        recetaGuardada.setId(1L);
        recetaGuardada.setRunCliente("12345678");
        recetaGuardada.setTipoReceta("Retenida");
        recetaGuardada.setFolioReceta("F123");
        recetaGuardada.setFechaEmision(request.getFechaEmision());
        recetaGuardada.setFechaVencimiento(request.getFechaVencimiento());
        recetaGuardada.setActiva(true);

        when(recetaClienteRepository.save(any(RecetaCliente.class))).thenReturn(recetaGuardada);

        // When
        RecetaClienteResponseDTO response = recetaClienteService.registrarReceta(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("12345678", response.getRunCliente());
        assertEquals("F123", response.getFolioReceta());
        assertTrue(response.isActiva());
        verify(recetaClienteRepository).save(any(RecetaCliente.class));
    }

    @Test
    public void registrarReceta_ConFolioDuplicado_DeberiaLanzarRecursoDuplicadoException() {
        // Given
        RecetaClienteRequestDTO request = new RecetaClienteRequestDTO();
        request.setRunCliente("12345678");
        request.setFolioReceta("F123");

        when(recetaClienteRepository.existsByRunClienteAndFolioReceta("12345678", "F123")).thenReturn(true);

        // When & Then
        assertThrows(RecursoDuplicadoException.class, () -> {
            recetaClienteService.registrarReceta(request);
        });
        verify(recetaClienteRepository, never()).save(any(RecetaCliente.class));
    }

    @Test
    public void registrarReceta_ConVentaLibre_DeberiaLanzarReglaNegocioException() {
        // Given
        RecetaClienteRequestDTO request = new RecetaClienteRequestDTO();
        request.setRunCliente("12345678");
        request.setFolioReceta("F123");
        request.setTipoReceta("venta libre");

        when(recetaClienteRepository.existsByRunClienteAndFolioReceta("12345678", "F123")).thenReturn(false);

        // When & Then
        assertThrows(ReglaNegocioException.class, () -> {
            recetaClienteService.registrarReceta(request);
        });
        verify(recetaClienteRepository, never()).save(any(RecetaCliente.class));
    }

    @Test
    public void registrarReceta_ConFechaVencimientoAnteriorAEmision_DeberiaLanzarReglaNegocioException() {
        // Given
        RecetaClienteRequestDTO request = new RecetaClienteRequestDTO();
        request.setRunCliente("12345678");
        request.setFolioReceta("F123");
        request.setTipoReceta("Retenida");
        request.setFechaEmision(LocalDate.now());
        request.setFechaVencimiento(LocalDate.now().minusDays(1));

        when(recetaClienteRepository.existsByRunClienteAndFolioReceta("12345678", "F123")).thenReturn(false);

        // When & Then
        assertThrows(ReglaNegocioException.class, () -> {
            recetaClienteService.registrarReceta(request);
        });
        verify(recetaClienteRepository, never()).save(any(RecetaCliente.class));
    }


    @Test
    public void registrarReceta_ConRequestNulo_DeberiaLanzarRecursoNuloException() {
        // Given & When & Then
        assertThrows(RecursoNuloException.class, () -> {
            recetaClienteService.registrarReceta(null);
        });
    }

    @Test
    public void listarPorRunCliente_DeberiaRetornarListaRecetas() {
        // Given
        String run = "12345678";
        RecetaCliente receta = new RecetaCliente();
        receta.setId(1L);
        receta.setRunCliente(run);
        receta.setTipoReceta("Retenida");
        receta.setFolioReceta("F123");
        receta.setActiva(true);

        when(recetaClienteRepository.findByRunClienteOrderByFechaEmisionDesc(run))
                .thenReturn(Collections.singletonList(receta));

        // When
        List<RecetaClienteResponseDTO> resultado = recetaClienteService.listarPorRunCliente(run);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("F123", resultado.get(0).getFolioReceta());
    }

    @Test
    public void obtenerPorId_Existente_DeberiaRetornarReceta() {
        // Given
        Long id = 1L;
        RecetaCliente receta = new RecetaCliente();
        receta.setId(id);
        receta.setRunCliente("12345678");
        receta.setTipoReceta("Retenida");
        receta.setFolioReceta("F123");
        receta.setActiva(true);

        when(recetaClienteRepository.findById(id)).thenReturn(Optional.of(receta));

        // When
        RecetaClienteResponseDTO response = recetaClienteService.obtenerPorId(id);

        // Then
        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    @Test
    public void obtenerPorId_NoExistente_DeberiaLanzarRecursoNoEncontradoException() {
        // Given
        Long id = 99L;
        when(recetaClienteRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            recetaClienteService.obtenerPorId(id);
        });
    }

    @Test
    public void desactivarReceta_Existente_DeberiaDesactivarYGuardar() {
        // Given
        Long id = 1L;
        RecetaCliente receta = new RecetaCliente();
        receta.setId(id);
        receta.setRunCliente("12345678");
        receta.setTipoReceta("Retenida");
        receta.setFolioReceta("F123");
        receta.setActiva(true);

        when(recetaClienteRepository.findById(id)).thenReturn(Optional.of(receta));
        when(recetaClienteRepository.save(any(RecetaCliente.class))).thenReturn(receta);

        // When
        recetaClienteService.desactivarReceta(id);

        // Then
        assertFalse(receta.isActiva());
        verify(recetaClienteRepository).save(receta);
    }

    @Test
    public void listarPorRunCliente_ConRunSinRecetas_DeberiaLanzarRecursoNoEncontradoException() {
        // Given
        String run = "12345678";
        when(recetaClienteRepository.findByRunClienteOrderByFechaEmisionDesc(run))
                .thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            recetaClienteService.listarPorRunCliente(run);
        });
    }

    @Test
    public void listarPorRunCliente_ConRunInexistente_DeberiaLanzarRecursoNoEncontradoException() {
        // Given
        String run = "87654321";
        when(clienteBeneficioClient.obtenerClientePorRun(run))
                .thenThrow(new RecursoNoEncontradoException("Cliente no encontrado con RUN: " + run));

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            recetaClienteService.listarPorRunCliente(run);
        });
        verify(recetaClienteRepository, never()).findByRunClienteOrderByFechaEmisionDesc(any(String.class));
    }
}
