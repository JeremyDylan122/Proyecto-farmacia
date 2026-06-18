package com.inventario.msinventario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventario.msinventario.dto.loteDTOs.LoteDTO;
import com.inventario.msinventario.dto.loteDTOs.LoteDTOMapper;
import com.inventario.msinventario.exceptions.RecursoNuloException;
import com.inventario.msinventario.model.Lote;
import com.inventario.msinventario.repository.LoteRepository;

@ExtendWith(MockitoExtension.class)
public class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;
    @Mock
    private LoteDTOMapper loteDTOMapper;
    @InjectMocks
    private LoteService service;

    // Test para verificar la busqueda por identificador unico
    @Test
    void givenCodigoLoteValido_whenBuscarPorId_thenReturnLoteDTO() {
        // GIVEN
        String codigoLote = "LOTE-001"; // se define codigo de lote
        Lote lote = new Lote(); // se instancia entidad modelo
        LoteDTO dto = new LoteDTO(); // se instancia dto de salida
        lote.setCodigoLote(codigoLote); // se vincula codigo al modelo

        // WHEN
        when(loteRepository.findById(codigoLote)).thenReturn(Optional.of(lote)); // mock encuentra el registro
        when(loteDTOMapper.toDTO(lote)).thenReturn(dto); // mock mapea a respuesta

        LoteDTO resultado = service.buscarPorId(codigoLote); // se ejecuta servicio

        // THEN
        assertNotNull(resultado); // se valida objeto no nulo
        verify(loteRepository, times(1)).findById(codigoLote); // se confirma consulta unitaria
    }

    // Test para verificar error al buscar con codigo en blanco o vacio
    @Test
    void givenCodigoLoteVacio_whenBuscarPorId_thenThrowRuntimeException() {
        // GIVEN
        String codigoLote = ""; // se define cadena vacia

        // WHEN AND THEN
        assertThrows(RuntimeException.class, () -> service.buscarPorId(codigoLote)); // se espera detencion por formato
        verify(loteRepository, never()).findById(anyString()); // se asegura el bloqueo pre-consulta
    }

    // Test para verificar error al buscar con parametro null
    @Test
    void givenCodigoLoteNulo_whenBuscarPorId_thenThrowRuntimeException() {
        // GIVEN
        String codigoLote = null; // se define parametro nulo

        // WHEN AND THEN
        assertThrows(RuntimeException.class, () -> service.buscarPorId(codigoLote)); // se espera excepcion de control
        verify(loteRepository, never()).findById(anyString()); // se evita pegarle a la base de datos
    }

    // Test para verificar error al buscar codigos que no estan registrados
    @Test
    void givenCodigoLoteNoExistente_whenBuscarPorId_thenThrowRuntimeException() {
        // GIVEN
        String codigoLote = "LOTE-999"; // se define id inexistente

        // WHEN
        when(loteRepository.findById(codigoLote)).thenReturn(Optional.empty()); // mock devuelve vacio

        // THEN
        assertThrows(RuntimeException.class, () -> service.buscarPorId(codigoLote)); // se valida el lanzamiento de excepcion
        verify(loteDTOMapper, never()).toDTO(any()); // se comprueba que no hubo mapeo
    }

    // Test para listar todos los lotes guardados en inventario
    @Test
    void givenLotesExistentes_whenListarTodos_thenReturnListaDTOs() {
        // GIVEN
        Lote lote = new Lote(); // se crea modelo
        LoteDTO dto = new LoteDTO(); // se crea dto
        List<Lote> listaSimulada = List.of(lote); // se arma coleccion de datos

        // WHEN
        when(loteRepository.findAll()).thenReturn(listaSimulada); // mock extrae informacion
        when(loteDTOMapper.toDTO(lote)).thenReturn(dto); // mock procesa transformaciones

        List<LoteDTO> resultado = service.listarTodos(); // se ejecuta servicio

        // THEN
        assertNotNull(resultado); // se valida inicializacion de lista
        assertEquals(1, resultado.size()); // se valida volumen de datos
        verify(loteRepository, times(1)).findAll(); // se audita lectura masiva
    }

    // Test para verificar listado maestro vacio
    @Test
    void givenSinLotesEnBD_whenListarTodos_thenReturnListaVacia() {
        // GIVEN
        List<Lote> listaVacia = List.of(); // se declara lista sin elementos

        // WHEN
        when(loteRepository.findAll()).thenReturn(listaVacia); // mock simula tabla vacia

        List<LoteDTO> resultado = service.listarTodos(); // se invoca el metodo

        // THEN
        assertNotNull(resultado); // se valida que no retorne nulo
        assertEquals(0, resultado.size()); // se confirma tamaño cero
        verify(loteRepository, times(1)).findAll(); // se comprueba la operacion
    }

    // Test para verificar error en creacion si el codigo viene vacio
    @Test
    void givenCodigoLoteVacio_whenCrearLote_thenThrowRecursoNuloException() {
        // GIVEN
        LoteDTO dtoInvalido = new LoteDTO(); // se prepara dto
        dtoInvalido.setCodigoLote(""); // se rompe restriccion ingresando vacio

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.crearLote(dtoInvalido)); //interrupcion defensiva

        verify(loteRepository, never()).existsById(any()); // se valida cancelacion inmediata de hilos
    }

    // Test para impedir el duplicado de llaves primarias de lotes
    @Test
    void givenCodigoLoteExistente_whenCrearLote_thenThrowRuntimeException() {
        // GIVEN
        LoteDTO dtoInvalido = new LoteDTO(); // se prepara entrada
        dtoInvalido.setCodigoLote("LOTE-EXISTENTE"); // se asigna codigo ocupado

        // WHEN
        when(loteRepository.existsById(dtoInvalido.getCodigoLote())).thenReturn(true); // mock detecta duplicado

        // THEN
        assertThrows(RuntimeException.class, () -> service.crearLote(dtoInvalido)); // se valida bloqueo de
                                                                                    // sobreescritura
        verify(loteRepository, never()).save(any()); // se comprueba que no se persistio
    }

    // Test para aplicar regla de negocio de vida util minima (vencimientos cortos)
    @Test
    void givenFechaVencimientoCorta_whenCrearLote_thenThrowRuntimeException() {
        // GIVEN
        LoteDTO dtoInvalido = new LoteDTO(); // se prepara dto
        dtoInvalido.setCodigoLote("LOTE-NUEVO"); // se pone id correcto
        dtoInvalido.setFechaVencimiento(LocalDate.now().plusMonths(6)); // infraccion: menor a un año de vida

        // WHEN
        when(loteRepository.existsById(dtoInvalido.getCodigoLote())).thenReturn(false); // mock aprueba id libre

        // THEN
        assertThrows(RuntimeException.class, () -> service.crearLote(dtoInvalido)); // se valida rechazo de mercaderia
                                                                                    // proxima a expirar
        verify(loteRepository, never()).save(any()); // se bloquea persistencia
    }

    // Test para validar que la referencia SKU no sea nula al crear
    @Test
    void givenSkuNulo_whenCrearLote_thenThrowRecursoNuloException() {
        // GIVEN
        LoteDTO dtoInvalido = new LoteDTO(); // se arma estructura
        dtoInvalido.setCodigoLote("LOTE-OK"); // id valido
        dtoInvalido.setFechaVencimiento(LocalDate.now().plusYears(2)); // fecha aprobada
        dtoInvalido.setSku(null); // infraccion: sin relacion de producto

        // WHEN
        when(loteRepository.existsById(dtoInvalido.getCodigoLote())).thenReturn(false); // mock valida id libre

        // THEN
        assertThrows(RecursoNuloException.class, () -> service.crearLote(dtoInvalido)); // falta integridad

        verify(loteRepository, never()).save(any()); // se resguarda bd
    }

    // Test para verificar FEFO consumiendo exactamente el stock total de un unico lote
    @Test
    void givenStockSuficienteEnPrimerLote_whenDescontarLotesPorFEFO_thenReturnPrimerCodigoLoteYDesactivar() {
        // GIVEN
        Long sku = 100L; // se define sku a procesar
        Integer cantidadAVender = 10; // demanda de venta

        Lote primerLote = new Lote(); // se inicializa el lote mas antiguo
        primerLote.setCodigoLote("LOTE-FEFO-01"); // se asigna identificador
        primerLote.setCantidad(10); // existencias coinciden exactamente con demanda
        primerLote.setActivo(true); // lote operativo

        List<Lote> listaLotes = List.of(primerLote); // se carga el lote en la cola de ejecucion

        // WHEN
        when(loteRepository.findByStockSkuAndActivoTrueOrderByFechaVencimientoAsc(sku)).thenReturn(listaLotes); // mock lote ordenado

        String resultado = service.descontarLotesPorFEFO(sku, cantidadAVender); // se ejecuta algoritmo

        // THEN
        assertEquals("LOTE-FEFO-01", resultado); // se verifica codigo retornado para el historico
        assertEquals(0, primerLote.getCantidad()); // saldo remanente debe quedar en cero
        assertFalse(primerLote.isActivo()); // lote debe darse de baja por agotamiento
        verify(loteRepository, times(1)).saveAll(listaLotes); // se audita impacto por lote modificado
    }

    // Test para verificar FEFO con descuento secuencial(multiples lotes)
    @Test
    void givenVentaSuperaPrimerLote_whenDescontarLotesPorFEFO_thenDescontarDeVariosLotes() {
        // GIVEN
        Long sku = 100L; // se define sku
        Integer cantidadAVender = 15; // demanda exige vaciar mas de un lote

        Lote lote1 = new Lote(); // lote proximo a vencer
        lote1.setCodigoLote("LOTE-VENCE-YA"); // se setea id
        lote1.setCantidad(10); // se definen existencias menores a la venta
        lote1.setActivo(true); // activo

        Lote lote2 = new Lote(); // lote secundario con vencimiento lejano
        lote2.setCodigoLote("LOTE-VENCE-LUEGO"); // se setea id
        lote2.setCantidad(20); // stock de respaldo
        lote2.setActivo(true); // activo

        List<Lote> listaLotes = List.of(lote1, lote2); // cola priorizada por FEFO

        // WHEN
        when(loteRepository.findByStockSkuAndActivoTrueOrderByFechaVencimientoAsc(sku)).thenReturn(listaLotes); // mock inyecta FEFO

        String resultado = service.descontarLotesPorFEFO(sku, cantidadAVender); // se arranca algoritmo iterativo

        // THEN
        assertEquals("LOTE-VENCE-YA", resultado); // debe retornar el id del primer lote golpeado
        assertEquals(0, lote1.getCantidad()); // lote primario queda vaciado completamente
        assertFalse(lote1.isActivo()); // lote primario dado de baja
        assertEquals(15, lote2.getCantidad()); // lote secundario absorbe el remanente (20 - 5)
        assertTrue(lote2.isActivo()); // lote secundario se conserva vigente
        verify(loteRepository, times(1)).saveAll(listaLotes); // se audita persistencia en lote batch
    }

    // Test para verificar error en FEFO cuando el SKU no posee lotes activos vigentes
    @Test
    void givenSinLotesActivos_whenDescontarLotesPorFEFO_thenThrowRuntimeException() {
        // GIVEN
        Long sku = 500L; // se define sku
        Integer cantidadAVender = 2; // demanda

        // WHEN
        when(loteRepository.findByStockSkuAndActivoTrueOrderByFechaVencimientoAsc(sku)).thenReturn(List.of()); // mock para quiebre o desabastecimiento

        // THEN
        assertThrows(RuntimeException.class, () -> service.descontarLotesPorFEFO(sku, cantidadAVender)); // rechazo por no insumos
        verify(loteRepository, never()).saveAll(any()); // se deniega ejecuciones de guardado
    }
}