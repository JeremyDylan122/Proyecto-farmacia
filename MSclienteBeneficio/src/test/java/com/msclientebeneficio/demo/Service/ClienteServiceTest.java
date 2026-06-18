package com.msclientebeneficio.demo.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.msclientebeneficio.demo.Dto.ClienteDTO;
import com.msclientebeneficio.demo.Dto.ClienteDTOMapper;
import com.msclientebeneficio.demo.Exception.RecursoNoEncontradoException;
import com.msclientebeneficio.demo.Exception.RecursoYaExisteException;
import com.msclientebeneficio.demo.Model.Cliente;
import com.msclientebeneficio.demo.Repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteDTOMapper clienteDTOMapper;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    public void obtenerClientePorRun_Existente_DeberiaRetornarDTO() {
        // Given
        String run = "12345678";
        Cliente cliente = new Cliente();
        cliente.setRun(run);
        
        ClienteDTO dto = new ClienteDTO();
        dto.setRun(run);

        when(clienteRepository.findByRun(run)).thenReturn(cliente);
        when(clienteDTOMapper.toDTO(cliente)).thenReturn(dto);

        // When
        ClienteDTO result = clienteService.obtenerClientePorRun(run);

        // Then
        assertNotNull(result);
        assertEquals(run, result.getRun());
        verify(clienteRepository).findByRun(run);
    }

    @Test
    public void obtenerClientePorRun_Inexistente_DeberiaLanzarRecursoNoEncontradoException() {
        // Given
        String run = "12345678";
        when(clienteRepository.findByRun(run)).thenReturn(null);

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            clienteService.obtenerClientePorRun(run);
        });
    }

    @Test
    public void crearCliente_Exitoso_DeberiaGuardarYRetornarDTO() {
        // Given
        String run = "12345678";
        ClienteDTO dtoInput = new ClienteDTO();
        dtoInput.setRun(run);

        Cliente cliente = new Cliente();
        cliente.setRun(run);

        ClienteDTO dtoOutput = new ClienteDTO();
        dtoOutput.setRun(run);

        when(clienteRepository.existsByRun(run)).thenReturn(false);
        when(clienteDTOMapper.toModel(dtoInput)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteDTOMapper.toDTO(cliente)).thenReturn(dtoOutput);

        // When
        ClienteDTO result = clienteService.crearCliente(dtoInput);

        // Then
        assertNotNull(result);
        assertEquals(run, result.getRun());
        verify(clienteRepository).save(cliente);
    }

    @Test
    public void crearCliente_YaExiste_DeberiaLanzarRecursoYaExisteException() {
        // Given
        String run = "12345678";
        ClienteDTO dtoInput = new ClienteDTO();
        dtoInput.setRun(run);

        when(clienteRepository.existsByRun(run)).thenReturn(true);

        // When & Then
        assertThrows(RecursoYaExisteException.class, () -> {
            clienteService.crearCliente(dtoInput);
        });
        verify(clienteRepository, never()).save(any(Cliente.class));
    }


    @Test
    public void eliminarCliente_Exitoso_DeberiaEliminarYRetornarTrue() {
        // Given
        String run = "12345678";
        String dv = "9";
        when(clienteRepository.existsByRun(run)).thenReturn(true);

        // When
        boolean result = clienteService.eliminarCliente(run, dv);

        // Then
        assertTrue(result);
        verify(clienteRepository).deleteByRunAndDv(run, dv);
    }

    @Test
    public void eliminarCliente_Inexistente_DeberiaLanzarRecursoNoEncontradoException() {
        // Given
        String run = "12345678";
        String dv = "9";
        when(clienteRepository.existsByRun(run)).thenReturn(false);

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            clienteService.eliminarCliente(run, dv);
        });
        verify(clienteRepository, never()).deleteByRunAndDv(run, dv);
    }

    @Test
    public void actualizarCliente_Exitoso_DeberiaActualizarYRetornarDTO() {
        // Given
        String run = "12345678";
        ClienteDTO dtoInput = new ClienteDTO();
        dtoInput.setCorreo("new@test.com");
        dtoInput.setTelefono("987654321");

        Cliente clienteExistente = new Cliente();
        clienteExistente.setRun(run);

        ClienteDTO dtoOutput = new ClienteDTO();
        dtoOutput.setRun(run);
        dtoOutput.setCorreo("new@test.com");

        when(clienteRepository.findByRun(run)).thenReturn(clienteExistente);
        when(clienteRepository.save(clienteExistente)).thenReturn(clienteExistente);
        when(clienteDTOMapper.toDTO(clienteExistente)).thenReturn(dtoOutput);

        // When
        ClienteDTO result = clienteService.actualizarCliente(run, dtoInput);

        // Then
        assertNotNull(result);
        assertEquals("new@test.com", result.getCorreo());
        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    public void actualizarCliente_Inexistente_DeberiaLanzarRecursoNoEncontradoException() {
        // Given
        String run = "12345678";
        ClienteDTO dtoInput = new ClienteDTO();
        when(clienteRepository.findByRun(run)).thenReturn(null);

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            clienteService.actualizarCliente(run, dtoInput);
        });
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
}
