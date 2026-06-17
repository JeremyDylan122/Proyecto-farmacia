package com.inventario.msinventario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventario.msinventario.dto.movimientoDTOs.MovimientoDTO;
import com.inventario.msinventario.dto.movimientoDTOs.MovimientoDTOMapper;
import com.inventario.msinventario.exceptions.RecursoNuloException;
import com.inventario.msinventario.model.Movimiento;
import com.inventario.msinventario.model.TipoMovimiento;
import com.inventario.msinventario.repository.MovimientoRepository;

@ExtendWith(MockitoExtension.class)
public class MovimientoServiceTest {

    @Mock
    private MovimientoRepository repository;
    @Mock
    private MovimientoDTOMapper movimientoMapper;

    @InjectMocks
    private MovimientoService service;

    // Test para verificar el listado con registros existentes
    @Test
    void givenMovimientoExistente_whenListarTodos_thenReturnListaConUnDTO() {
        // GIVEN
        Movimiento movimiento = new Movimiento(); // se crea entidad
        MovimientoDTO dto = new MovimientoDTO(); // se crea dto
        List<Movimiento> listaSimulada = List.of(movimiento); // se arma lista mock

        // WHEN
        when(repository.findAll()).thenReturn(listaSimulada); // mock busca todos
        when(movimientoMapper.toDTO(movimiento)).thenReturn(dto); // mock mapeo a dto

        List<MovimientoDTO> resultado = service.listarTodos(); // se ejecuta servicio

        // THEN
        assertNotNull(resultado); // se valida que no sea nulo
        assertEquals(1, resultado.size()); // se valida tamaño de lista
        verify(repository, times(1)).findAll(); // se verifica llamada a bd
        verify(movimientoMapper, times(1)).toDTO(movimiento); // se verifica mapeo
    }

    // Test para verificar error al intentar registrar datos nulos
    @Test
    void givenMovimientoDTONulo_whenRegistrarMovimiento_thenThrowRuntimeException() {
        // GIVEN
        MovimientoDTO movimientoDTO = null; // se define entrada nula

        // WHEN AND THEN
        assertThrows(RuntimeException.class, () -> service.registrarMovimiento(movimientoDTO)); // se espera excepcion
        verify(repository, atMostOnce()).save(any()); // se verifica flujo defensivo
    }

    // Test para verificar el listado cuando no hay registros en bd
    @Test
    void givenSinMovimientosEnBD_whenListarTodos_thenReturnListaVacia() {
        // GIVEN
        List<Movimiento> lista = List.of(); // se arma lista vacia

        // WHEN
        when(repository.findAll()).thenReturn(lista); // mock busca todos vacio

        List<MovimientoDTO> resultado = service.listarTodos(); // se ejecuta servicio

        // THEN
        assertNotNull(resultado); // se valida objeto inicializado
        assertEquals(0, resultado.size()); // se valida lista sin elementos
        verify(repository, atMostOnce()).findAll(); // se verifica llamada a bd
    }

    // Test para verificar busqueda exitosa por identificador
    @Test
    void givenIdMovimientoValido_whenListarPorId_thenReturnMovimientoDTO() {
        // GIVEN
        Long idMovimiento = 1L; // se define id valido
        Movimiento movimiento = new Movimiento(); // se crea entidad
        MovimientoDTO dto = new MovimientoDTO(); // se crea dto

        when(repository.findById(idMovimiento)).thenReturn(Optional.of(movimiento)); // mock encuentra id
        when(movimientoMapper.toDTO(movimiento)).thenReturn(dto); // mock mapeo a dto

        // WHEN
        MovimientoDTO resultado = service.listarPorId(idMovimiento); // se ejecuta servicio

        // THEN
        assertNotNull(resultado); // se valida retorno del dto
        verify(repository, atMostOnce()).findById(idMovimiento); // se verifica llamada a bd
    }

    // Test para verificar registro exitoso de un movimiento nuevo
    @Test
    void givenMovimientoDTOValido_whenRegistrarMovimiento_thenReturnMovimientoDTO() {
        // GIVEN
        MovimientoDTO dtoInput = new MovimientoDTO(); // se crea dto entrada
        dtoInput.setTipo(TipoMovimiento.COMPRA); // se setea tipo compra
        dtoInput.setCodigoLote("LOTE-001"); // se setea lote de prueba

        Movimiento modeloIntermedio = new Movimiento(); // se crea modelo para mapeo
        Movimiento modeloGuardado = new Movimiento(); // se crea modelo guardado
        modeloGuardado.setIdMovimiento(1L); // se asigna id simulado

        MovimientoDTO dtoOutput = new MovimientoDTO(); // se crea dto salida

        when(movimientoMapper.toModel(dtoInput)).thenReturn(modeloIntermedio); // mock mapeo a modelo
        when(repository.save(modeloIntermedio)).thenReturn(modeloGuardado); // mock guarda en bd
        when(movimientoMapper.toDTO(modeloGuardado)).thenReturn(dtoOutput); // mock mapeo a dto

        // WHEN
        MovimientoDTO resultado = service.registrarMovimiento(dtoInput); // se ejecuta servicio

        // THEN
        assertNotNull(resultado); // se valida que retorne datos
        verify(repository, times(1)).save(modeloIntermedio); // se verifica persistencia
        verify(movimientoMapper, times(1)).toModel(dtoInput); // se verifica mapeo entrada
        verify(movimientoMapper, times(1)).toDTO(modeloGuardado); // se verifica mapeo salida
    }

    // Test para verificar error al buscar con identificador nulo
    @Test
    void givenIdMovimientoNulo_whenListarPorId_thenThrowRecursoNuloException() {
        // GIVEN
        Long idMovimiento = null; // se define id nulo

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.listarPorId(idMovimiento)); // se espera excepcion nulo
        verify(repository, atMostOnce()).findById(idMovimiento); // se verifica ejecucion segura
    }

    // Test para verificar error al buscar un identificador inesistente
    @Test
    void givenIdNoExistente_whenListarPorId_thenThrowRuntimeException() {
        // GIVEN
        Long idMovimiento = 99L; // se define id fantasma

        // WHEN
        when(repository.findById(idMovimiento)).thenReturn(Optional.empty()); // mock no encuentra nada

        // THEN
        assertThrows(RuntimeException.class, () -> service.listarPorId(idMovimiento)); // se espera excepcion faltante
        verify(movimientoMapper, atMostOnce()).toDTO(any()); // se verifica que no mapeo
    }

}