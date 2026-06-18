package com.inventario.msinventario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

import com.inventario.msinventario.dto.stockDTOs.StockDTO;
import com.inventario.msinventario.dto.stockDTOs.StockDTOMapper;
import com.inventario.msinventario.exceptions.RecursoNuloException;
import com.inventario.msinventario.exceptions.StockInsuficienteException;
import com.inventario.msinventario.model.Stock;
import com.inventario.msinventario.repository.StockRepository;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private StockDTOMapper stockDTOMapper;
    @InjectMocks
    private StockService service;

    // Test para verificar la busqueda exitosa por sku
    @Test
    void givenSkuValido_whenBuscarPorSku_thenReturnStockDTO() {
        // GIVEN
        Long sku = 1L; // se define sku de prueba
        Stock stock = new Stock(); // se crea entidad stock vacia
        StockDTO dto = new StockDTO(); // se crea dto de respuesta

        // WHEN
        when(stockRepository.findById(sku)).thenReturn(Optional.of(stock)); // mock encuentra el sku
        when(stockDTOMapper.toDTO(stock)).thenReturn(dto); // mock realiza el mapeo

        StockDTO resultado = service.buscarPorSku(sku); // se ejecuta el metodo

        // THEN
        assertNotNull(resultado); // se valida que no retorne nulo
        verify(stockRepository, times(1)).findById(sku); // se verifica acceso a base de datos
    }

    // Test para verificar error al buscar con sku nulo
    @Test
    void givenSkuNulo_whenBuscarPorSku_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = null; // se define parametro nulo

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.buscarPorSku(sku)); // se espera excepcion por nulo
        verify(stockRepository, never()).findById(any()); // se verifica proteccion de bd
    }

    // Test para verificar error al buscar un sku inexistente en base de datos
    @Test
    void givenSkuNoExistente_whenBuscarPorSku_thenThrowRuntimeException() {
        // GIVEN
        Long sku = 1L; // se define sku valido

        // WHEN
        when(stockRepository.findById(sku)).thenReturn(Optional.empty()); // mock no encuentra el registro

        // THEN
        assertThrows(RuntimeException.class, () -> service.buscarPorSku(sku)); // se espera excepcion de no encontrado
        verify(stockDTOMapper, never()).toDTO(any()); // se verifica que no se mapeo nada
    }

    // Test para verificar el listado completo de stock disponible
    @Test
    void givenStockExistente_whenListarStock_thenReturnListaDTOs() {
        // GIVEN
        Stock stock = new Stock(); // se crea entidad stock
        StockDTO dto = new StockDTO(); // se crea dto stock
        List<Stock> listaSimulada = List.of(stock); // se arma lista con datos

        // WHEN
        when(stockRepository.findAll()).thenReturn(listaSimulada); // mock busca todos los registros
        when(stockDTOMapper.toDTO(stock)).thenReturn(dto); // mock realiza el mapeo de la lista

        List<StockDTO> resultado = service.listarStock(); // se ejecuta el metodo

        // THEN
        assertNotNull(resultado); // se valida que la lista no sea nula
        assertEquals(1, resultado.size()); // se valida la cantidad de elementos
        verify(stockRepository, times(1)).findAll(); // se verifica consulta a la bd
    }

    // Test para verificar el listado cuando no hay balances guardados
    @Test
    void givenSinStockEnBD_whenListarStock_thenReturnListaVacia() {
        // GIVEN
        List<Stock> listaVacia = List.of(); // se arma lista vacia de base de datos

        // WHEN
        when(stockRepository.findAll()).thenReturn(listaVacia); // mock retorna consulta vacia

        List<StockDTO> resultado = service.listarStock(); // se ejecuta el metodo

        // THEN
        assertNotNull(resultado); // se valida que el objeto retornado exista
        assertEquals(0, resultado.size()); // se valida lista sin elementos
        verify(stockRepository, times(1)).findAll(); // se verifica ejecucion en bd
    }

    // Test para verificar la inicializacion de un producto nuevo en bodega
    @Test
    void givenSkuNuevo_whenAcumularOInicializarStock_thenReturnNewStockDTO() {
        // GIVEN
        Long sku = 1L; // se define nuevo sku
        Integer cantidadEntrante = 5; // se define cantidad inicial
        Stock stockGuardado = new Stock(); // se simula modelo guardado
        StockDTO dto = new StockDTO(); // se crea dto de salida

        // WHEN
        when(stockRepository.findById(sku)).thenReturn(Optional.empty()); // mock confirma que es producto nuevo
        when(stockRepository.save(any(Stock.class))).thenReturn(stockGuardado); // mock persiste inicializacion
        when(stockDTOMapper.toDTO(stockGuardado)).thenReturn(dto); // mock mapea resultado

        StockDTO resultado = service.acumularOInicializarStock(sku, cantidadEntrante); // se ejecuta el metodo

        // THEN
        assertNotNull(resultado); // se valida respuesta del servicio
        verify(stockRepository, times(1)).findById(sku); // se verifica validacion de existencia
        verify(stockRepository, times(1)).save(any(Stock.class)); // se verifica almacenamiento
    }

    // Test para verificar la acumulacion de stock sobre un producto ya registrado
    @Test
    void givenSkuExistente_whenAcumularOInicializarStock_thenReturnUpdatedStockDTO() {
        // GIVEN
        Long sku = 1L; // se define sku existente
        Integer cantidadEntrante = 10; // se define cantidad a sumar
        Stock stockExistente = new Stock(); // se crea entidad preexistente
        stockExistente.setSku(sku); // se asigna sku preexistente
        stockExistente.setCantidadTotal(20); // se asigna saldo anterior

        Stock stockGuardado = new Stock(); // se simula modelo guardado con suma
        StockDTO dto = new StockDTO(); // se crea dto de salida

        // WHEN
        when(stockRepository.findById(sku)).thenReturn(Optional.of(stockExistente)); // mock encuentra saldo previo
        when(stockRepository.save(any(Stock.class))).thenReturn(stockGuardado); // mock guarda actualizacion
        when(stockDTOMapper.toDTO(stockGuardado)).thenReturn(dto); // mock mapea resultado

        StockDTO resultado = service.acumularOInicializarStock(sku, cantidadEntrante); // se ejecuta el metodo

        // THEN
        assertNotNull(resultado); // se valida retorno correcto
        verify(stockRepository, times(1)).findById(sku); // se verifica lectura de saldo
        verify(stockRepository, times(1)).save(any(Stock.class)); // se verifica guardado de suma
    }

    // Test para verificar error en inicializacion con sku nulo
    @Test
    void givenSkuNulo_whenAcumularOInicializarStock_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = null; // se define sku nulo
        Integer cantidadEntrante = 5; // se define cantidad valida

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.acumularOInicializarStock(sku, cantidadEntrante)); // se
                                                                                                                  // valida
                                                                                                                  // control
                                                                                                                  // defensivo
        verify(stockRepository, never()).findById(any()); // se comprueba aborto seguro
    }

    // Test para verificar error en inicializacion con cantidad nula
    @Test
    void givenCantidadNula_whenAcumularOInicializarStock_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = 1L; // se define sku valido
        Integer cantidadEntrante = null; // se define cantidad nula

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.acumularOInicializarStock(sku, cantidadEntrante)); // se
                                                                                                                  // valida
                                                                                                                  // control
                                                                                                                  // de
                                                                                                                  // nulos
        verify(stockRepository, never()).findById(any()); // se comprueba aborto seguro
    }

    // Test para verificar error cuando la cantidad entrante es menor a uno
    @Test
    void givenCantidadInvalida_whenAcumularOInicializarStock_thenThrowRuntimeException() {
        // GIVEN
        Long sku = 1L; // se define sku valido
        Integer cantidadInvalida = 0; // se define cantidad menor al limite

        // WHEN AND THEN
        assertThrows(RuntimeException.class, () -> service.acumularOInicializarStock(sku, cantidadInvalida)); // se
                                                                                                              // valida
                                                                                                              // regla
                                                                                                              // de
                                                                                                              // negocio
                                                                                                              // cantidad
        verify(stockRepository, never()).findById(any()); // se comprueba bloqueo previo
    }

    // Test para verificar el descuento de stock correcto tras una venta
    @Test
    void givenVentaValida_whenDescontarStockPorVenta_thenReturnUpdatedStockDTO() {
        // GIVEN
        Long sku = 1L; // se define sku a vender
        Integer cantidadAVender = 5; // se define cantidad de venta
        Stock stockExistente = new Stock(); // se crea balance previo
        stockExistente.setSku(sku); // se asigna sku
        stockExistente.setCantidadTotal(15); // se asigna stock suficiente

        Stock stockGuardado = new Stock(); // se simula balance disminuido
        StockDTO dto = new StockDTO(); // se crea dto salida

        // WHEN
        when(stockRepository.findById(sku)).thenReturn(Optional.of(stockExistente)); // mock encuentra mercaderia
        when(stockRepository.save(any(Stock.class))).thenReturn(stockGuardado); // mock guarda reduccion
        when(stockDTOMapper.toDTO(stockGuardado)).thenReturn(dto); // mock mapea salida

        StockDTO resultado = service.descontarStockPorVenta(sku, cantidadAVender); // se ejecuta el metodo

        // THEN
        assertNotNull(resultado); // se valida procesamiento exitoso
        verify(stockRepository, times(1)).findById(sku); // se verifica validacion fisica
        verify(stockRepository, times(1)).save(any(Stock.class)); // se decontaron las unidades
    }

    // Test para verificar error en venta con sku nulo
    @Test
    void givenSkuNulo_whenDescontarStockPorVenta_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = null; // se define sku nulo
        Integer cantidadAVender = 5; // se define cantidad valida

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.descontarStockPorVenta(sku, cantidadAVender)); // se
                                                                                                              // valida
                                                                                                              // filtro
                                                                                                              // inicial
                                                                                                              // nulos
        verify(stockRepository, never()).findById(any()); // se comprueba cancelacion automatica
    }

    // Test para verificar error en venta con cantidad vacia o nula
    @Test
    void givenCantidadNula_whenDescontarStockPorVenta_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = 1L; // se define sku valido
        Integer cantidadAVender = null; // se define cantidad nula

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.descontarStockPorVenta(sku, cantidadAVender)); // se
                                                                                                              // valida
                                                                                                              // filtro
                                                                                                              // de
                                                                                                              // parametros
                                                                                                              // nulos
        verify(stockRepository, never()).findById(any()); // se comprueba cancelacion automatica
    }

    // Test para verificar rechazo de venta cuando el producto no existe en bodega
    @Test
    void givenSkuInexistente_whenDescontarStockPorVenta_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = 1L; // se define sku de prueba
        Integer cantidadAVender = 5; // se define demanda

        // WHEN
        when(stockRepository.findById(sku)).thenReturn(Optional.empty()); // mock no encuentra producto en stock

        // THEN
        assertThrows(RecursoNuloException.class, () -> service.descontarStockPorVenta(sku, cantidadAVender)); // se
                                                                                                              // valida
                                                                                                              // excepcion
                                                                                                              // del
                                                                                                              // producto
                                                                                                              // fantasma
        verify(stockRepository, never()).save(any()); // se garantiza que no se altero la bd
    }

    // Test para verificar rechazo de venta cuando la demanda supera las existencias
    // reales
    @Test
    void givenStockInsuficiente_whenDescontarStockPorVenta_thenThrowStockInsuficienteException() {
        // GIVEN
        Long sku = 1L; // se define sku valido
        Integer cantidadAVender = 10; // se solicita mas de lo disponible
        Stock stockInsuficiente = new Stock(); // se crea balance critico
        stockInsuficiente.setSku(sku); // se setea sku
        stockInsuficiente.setCantidadTotal(3); // stock real en bodega insuficiente

        // WHEN
        when(stockRepository.findById(sku)).thenReturn(Optional.of(stockInsuficiente)); // mock lee stock critico

        // THEN
        assertThrows(StockInsuficienteException.class, () -> service.descontarStockPorVenta(sku, cantidadAVender)); // se
                                                                                                                    // valida
                                                                                                                    // quiebre
                                                                                                                    // de
                                                                                                                    // stock
        verify(stockRepository, never()).save(any()); // se deniega el almacenamiento del descuento
    }
}