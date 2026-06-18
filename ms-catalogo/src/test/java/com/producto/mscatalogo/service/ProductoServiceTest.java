package com.producto.mscatalogo.service;

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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.producto.mscatalogo.dto.ProductoDTO;
import com.producto.mscatalogo.dto.ProductoDTOMapper;
import com.producto.mscatalogo.exceptions.RecursoDuplicadoException;
import com.producto.mscatalogo.exceptions.RecursoNoEncontradoException;
import com.producto.mscatalogo.exceptions.RecursoNuloException;
import com.producto.mscatalogo.model.Categoria;
import com.producto.mscatalogo.model.Producto;
import com.producto.mscatalogo.model.TipoReceta;
import com.producto.mscatalogo.repository.CategoriaRepository;
import com.producto.mscatalogo.repository.ProductoRepository;
import com.producto.mscatalogo.repository.TipoRecetaRepository;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository; // mock del repositorio de productos
    @Mock
    private CategoriaRepository categoriaRepository; // mock del repositorio de categorias
    @Mock
    private TipoRecetaRepository tipoRecetaRepository; // mock del repositorio de recetas
    @Mock
    private ProductoDTOMapper productoDTOMapper; // mock del mapper de productos
    @InjectMocks
    private ProductoService service; // inyeccion del servicio bajo prueba

    // Test para verificar el listado completo de productos en catalogo
    @Test
    void givenProductosExistentes_whenListarTodos_thenReturnListaDTOs() {
        // GIVEN
        Producto producto = new Producto(); // se crea entidad producto
        ProductoDTO dto = new ProductoDTO(); // se crea dto producto
        List<Producto> listaSimulada = List.of(producto); // se arma lista con datos

        // WHEN
        when(productoRepository.findAll()).thenReturn(listaSimulada); // mock busca todos los registros
        when(productoDTOMapper.toDTO(producto)).thenReturn(dto); // mock mapea a dto salida

        List<ProductoDTO> resultado = service.listarTodos(); // se ejecuta el metodo

        // THEN
        assertNotNull(resultado); // se valida lista inicializada
        assertEquals(1, resultado.size()); // se valida tamaño de lista
        verify(productoRepository, times(1)).findAll(); // se verifica consulta remota
    }

    // Test para verificar listado de productos vacio
    @Test
    void givenSinProductosEnBD_whenListarTodos_thenReturnListaVacia() {
        // GIVEN
        List<Producto> listaVacia = List.of(); // se arma lista vacia de bd

        // WHEN
        when(productoRepository.findAll()).thenReturn(listaVacia); // mock devuelve tabla vacia

        List<ProductoDTO> resultado = service.listarTodos(); // se ejecuta el metodo

        // THEN
        assertNotNull(resultado); // se valida objeto retornado existente
        assertEquals(0, resultado.size()); // se valida lista sin elementos
        verify(productoRepository, times(1)).findAll(); // se verifica ejecucion de hilos
    }

    // Test para verificar busqueda exitosa por sku
    @Test
    void givenSkuValido_whenBuscarPorSku_thenReturnProductoDTO() {
        // GIVEN
        Long sku = 100L; // se define sku valido
        Producto producto = new Producto(); // se crea modelo
        ProductoDTO dto = new ProductoDTO(); // se crea dto

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(producto); // mock encuentra el registro
        when(productoDTOMapper.toDTO(producto)).thenReturn(dto); // mock realiza el mapeo

        ProductoDTO resultado = service.buscarPorSku(sku); // se ejecuta servicio

        // THEN
        assertNotNull(resultado); // se valida retorno correcto
        verify(productoRepository, times(1)).findBySku(sku); // se audita control de flujo bd
    }

    // Test para verificar error al buscar con parametro sku nulo
    @Test
    void givenSkuNulo_whenBuscarPorSku_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = null; // se define parametro nulo

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.buscarPorSku(sku)); // se espera excepcion defensiva
        verify(productoRepository, never()).findBySku(any()); // se comprueba aborto seguro
    }

    // Test para verificar error al buscar sku no registrado
    @Test
    void givenSkuNoExistente_whenBuscarPorSku_thenThrowRecursoNoEncontradoException() {
        // GIVEN
        Long sku = 999L; // se define sku fantasma

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(null); // mock simula registro inexistente

        // THEN
        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorSku(sku)); // se espera excepcion de ausencia
        verify(productoDTOMapper, never()).toDTO(any()); // se resguarda el mapper
    }

    // Test para verificar listado correcto por filtro de categoria
    @Test
    void givenCategoriaExistente_whenListarPorCategoria_thenReturnListaDTOs() {
        // GIVEN
        String cat = "FARMACIA"; // se define categoria de entrada
        Producto producto = new Producto(); // se crea modelo
        ProductoDTO dto = new ProductoDTO(); // se crea dto salida
        List<Producto> listaSimulada = List.of(producto); // se arma coleccion

        // WHEN
        when(categoriaRepository.existsByNombreIgnoreCase(cat)).thenReturn(true); // mock valida que existe categoria
        when(productoRepository.findByCategoriaNombreIgnoreCase(cat)).thenReturn(listaSimulada); // mock busca por string
        when(productoDTOMapper.toDTO(producto)).thenReturn(dto); // mock mapea elementos

        List<ProductoDTO> resultado = service.listarPorCategoria(cat); // se ejecuta servicio

        // THEN
        assertNotNull(resultado); // se valida respuesta inicializada
        assertEquals(1, resultado.size()); // se valida volumen de datos
        verify(productoRepository, times(1)).findByCategoriaNombreIgnoreCase(cat); // se confirma operacion de filtro
    }

    // Test para verificar error al filtrar por categoria que no existe
    @Test
    void givenCategoriaNoExistente_whenListarPorCategoria_thenThrowRecursoNoEncontradoException() {
        // GIVEN
        String cat = "MINI-MARKET"; // se ingresa categoria invalida

        // WHEN
        when(categoriaRepository.existsByNombreIgnoreCase(cat)).thenReturn(false); // mock rechaza existencia

        // THEN
        assertThrows(RecursoNoEncontradoException.class, () -> service.listarPorCategoria(cat)); // se espera interrupcion del flujo
        verify(productoRepository, never()).findByCategoriaNombreIgnoreCase(anyString()); // se bloquea busqueda subsecuente
    }

    // Test para verificar el registro de un producto con todas sus dependencias validas
    @Test
    void givenProductoDTOValido_whenAgregarProducto_thenReturnProductoDTO() {
        // GIVEN
        ProductoDTO inputDTO = new ProductoDTO(); // se crea dto entrada
        inputDTO.setSku(1010L); // se asocia sku
        inputDTO.setCategoria("FARMACIA"); // se asocia categoria
        inputDTO.setTipoReceta("RETENIDA"); // se asocia receta

        Categoria categoria = new Categoria(); // se crea modelo categoria
        TipoReceta receta = new TipoReceta(); // se crea modelo receta
        Producto modeloIntermedio = new Producto(); // se crea modelo limpio para mapper
        Producto modeloGuardado = new Producto(); // se crea modelo guardado
        ProductoDTO outputDTO = new ProductoDTO(); // se crea dto salida

        // WHEN
        when(productoRepository.existsBySku(inputDTO.getSku())).thenReturn(false); // mock valida sku disponible
        when(categoriaRepository.findByNombreIgnoreCase(inputDTO.getCategoria())).thenReturn(categoria); // mock encuentra categoria
        when(tipoRecetaRepository.findByNombreIgnoreCase(inputDTO.getTipoReceta())).thenReturn(receta); // mock encuentra receta
        when(productoDTOMapper.toModel(inputDTO)).thenReturn(modeloIntermedio); // mock transforma entrada
        when(productoRepository.save(modeloIntermedio)).thenReturn(modeloGuardado); // mock guarda producto
        when(productoDTOMapper.toDTO(modeloGuardado)).thenReturn(outputDTO); // mock genera respuesta

        ProductoDTO resultado = service.agregarProducto(inputDTO); // se ejecuta el metodo

        // THEN
        assertNotNull(resultado); // se valida consistencia de respuesta
        verify(productoRepository, times(1)).save(modeloIntermedio); // se confirma almacenamiento definitivo
    }

    // Test para bloquear la creacion de productos con skus duplicados
    @Test
    void givenSkuDuplicado_whenAgregarProducto_thenThrowRecursoDuplicadoException() {
        // GIVEN
        ProductoDTO inputDTO = new ProductoDTO(); // se prepara dto
        inputDTO.setSku(1010L); // se ingresa sku ocupado

        // WHEN
        when(productoRepository.existsBySku(inputDTO.getSku())).thenReturn(true); // mock acusa duplicidad

        // THEN
        assertThrows(RecursoDuplicadoException.class, () -> service.agregarProducto(inputDTO)); // se valida corte transaccional
        verify(productoRepository, never()).save(any()); // se protege almacenamiento
    }

    // Test para bloquear creacion de producto con categoria inexistente
    @Test
    void givenCategoriaNoExistente_whenAgregarProducto_thenThrowRecursoNoEncontradoException() {
        // GIVEN
        ProductoDTO inputDTO = new ProductoDTO(); // se prepara dto
        inputDTO.setSku(1010L); // sku aprobado
        inputDTO.setCategoria("MUTANTES"); // categoria falsa

        // WHEN
        when(productoRepository.existsBySku(inputDTO.getSku())).thenReturn(false); // mock aprueba sku libre
        when(categoriaRepository.findByNombreIgnoreCase(inputDTO.getCategoria())).thenReturn(null); // mock no encuentra la categoria

        // THEN
        assertThrows(RecursoNoEncontradoException.class, () -> service.agregarProducto(inputDTO)); // se interrumpe por integridad relacional
        verify(tipoRecetaRepository, never()).findByNombreIgnoreCase(anyString()); // se suspenden chequeos posteriores
    }

    // Test para bloquear creacion de producto con receta inexistente
    @Test
    void givenRecetaNoExistente_whenAgregarProducto_thenThrowRecursoNoEncontradoException() {
        // GIVEN
        ProductoDTO inputDTO = new ProductoDTO(); // se prepara dto
        inputDTO.setSku(1010L); // sku aprobado
        inputDTO.setCategoria("FARMACIA"); // categoria aprobada
        inputDTO.setTipoReceta("MAGICA"); // receta falsa

        Categoria categoria = new Categoria(); // modelo categoria auxiliar

        // WHEN
        when(productoRepository.existsBySku(inputDTO.getSku())).thenReturn(false); // mock aprueba sku
        when(categoriaRepository.findByNombreIgnoreCase(inputDTO.getCategoria())).thenReturn(categoria); // mock aprueba categoria
        when(tipoRecetaRepository.findByNombreIgnoreCase(inputDTO.getTipoReceta())).thenReturn(null); // mock rechaza receta

        // THEN
        assertThrows(RecursoNoEncontradoException.class, () -> service.agregarProducto(inputDTO)); // se espera excepcion por receta
        verify(productoRepository, never()).save(any()); // se rechaza persistencia
    }

    // Test para verificar modificacion completa de un producto existente
    @Test
    void givenSkuYDTOValido_whenActualizarProducto_thenReturnProductoDTO() {
        // GIVEN
        Long sku = 100L; // sku objetivo
        ProductoDTO inputDTO = new ProductoDTO(); // datos nuevos
        inputDTO.setCategoria("FARMACIA"); // categoria actualizacion
        inputDTO.setTipoReceta("VENTA-LIBRE"); // receta actualizacion

        Producto productoExistente = new Producto(); // entidad bd antigua
        Categoria categoria = new Categoria(); // modelo categoria
        TipoReceta receta = new TipoReceta(); // modelo receta
        Producto productoActualizado = new Producto(); // entidad modificada
        ProductoDTO outputDTO = new ProductoDTO(); // respuesta final

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(productoExistente); // mock localiza el registro a alterar
        when(categoriaRepository.findByNombreIgnoreCase(inputDTO.getCategoria())).thenReturn(categoria); // mock valida nueva categoria
        when(tipoRecetaRepository.findByNombreIgnoreCase(inputDTO.getTipoReceta())).thenReturn(receta); // mock valida nueva receta
        when(productoRepository.save(productoExistente)).thenReturn(productoActualizado); // mock guarda los cambios estructurales
        when(productoDTOMapper.toDTO(productoActualizado)).thenReturn(outputDTO); // mock procesa mapeo salida

        ProductoDTO resultado = service.actualizarProducto(sku, inputDTO); // se ejecuta servicio

        // THEN
        assertNotNull(resultado); // se valida actualizacion exitosa
        verify(productoRepository, times(1)).save(productoExistente); // se confirma guardado de modificaciones
    }

    // Test para bloquear actualizacion si el sku provisto es nulo
    @Test
    void givenSkuNulo_whenActualizarProducto_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = null; // parametro nulo
        ProductoDTO dto = new ProductoDTO(); // dto datos

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.actualizarProducto(sku, dto)); // se valida proteccion inicial nulo
        verify(productoRepository, never()).findBySku(any()); // se detiene operacion en seco
    }

    // Test para impedir actualizacion de productos que no estan en el catalogo
    @Test
    void givenSkuNoExistente_whenActualizarProducto_thenThrowRecursoNoEncontradoException() {
        // GIVEN
        Long sku = 999L; // sku no registrado
        ProductoDTO dto = new ProductoDTO(); // dto datos

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(null); // mock acusa registro inexistente

        // THEN
        assertThrows(RecursoNoEncontradoException.class, () -> service.actualizarProducto(sku, dto)); // se valida excepcion requerida
        verify(categoriaRepository, never()).findByNombreIgnoreCase(anyString()); // se cancelan mapeos relacionales
    }

    // Test para bloquear actualizacion si la nueva categoria ingresada es invalida
    @Test
    void givenCategoriaInvalidaEnDTO_whenActualizarProducto_thenThrowRecursoNoEncontradoException() {
        // GIVEN
        Long sku = 100L; // sku valido
        ProductoDTO dto = new ProductoDTO(); // dto datos
        dto.setCategoria("FALSA"); // categoria erronea

        Producto producto = new Producto(); // modelo bd existente

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(producto); // mock aprueba localizacion producto
        when(categoriaRepository.findByNombreIgnoreCase(dto.getCategoria())).thenReturn(null); // mock acusa categoria invalida

        // THEN
        assertThrows(RecursoNoEncontradoException.class, () -> service.actualizarProducto(sku, dto)); // se detiene flujo por categoria
        verify(tipoRecetaRepository, never()).findByNombreIgnoreCase(anyString()); // se suspenden siguientes chequeos
    }

    // Test para bloquear actualizacion si la nueva receta ingresada es invalida
    @Test
    void givenRecetaInvalidaEnDTO_whenActualizarProducto_thenThrowRecursoNoEncontradoException() {
        // GIVEN
        Long sku = 100L; // sku valido
        ProductoDTO dto = new ProductoDTO(); // dto datos
        dto.setCategoria("FARMACIA"); // categoria correcta
        dto.setTipoReceta("FALSA"); // receta erronea

        Producto producto = new Producto(); // modelo bd existente
        Categoria categoria = new Categoria(); // modelo categoria auxiliar

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(producto); // mock aprueba localizacion producto
        when(categoriaRepository.findByNombreIgnoreCase(dto.getCategoria())).thenReturn(categoria); // mock aprueba categoria
        when(tipoRecetaRepository.findByNombreIgnoreCase(dto.getTipoReceta())).thenReturn(null); // mock rechaza receta nueva

        // THEN
        assertThrows(RecursoNoEncontradoException.class, () -> service.actualizarProducto(sku, dto)); // se detiene por regla receta
        verify(productoRepository, never()).save(any()); // se deniega guardado corrupto
    }

    // Test para verificar la baja logica exitosa de un producto
    @Test
    void givenSkuValido_whenOcultarProducto_thenSetActivoFalseYGuardar() {
        // GIVEN
        Long sku = 100L; // sku a dar de baja
        Producto producto = new Producto(); // se crea modelo bd
        producto.setActivo(true); // se inicia con estado activo

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(producto); // mock encuentra producto activo

        service.ocultarProducto(sku); // se ejecuta baja logica

        // THEN
        assertFalse(producto.isActivo()); // se valida alteracion del flag a inactivo
        verify(productoRepository, times(1)).save(producto); // se confirma persistencia del cambio de estado
    }

    // Test para bloquear ocultamiento por sku nulo
    @Test
    void givenSkuNulo_whenOcultarProducto_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = null; // parametro nulo

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.ocultarProducto(sku)); // se espera captura por nulidad
        verify(productoRepository, never()).findBySku(any()); // se protege acceso a bd
    }

    // Test para verificar error al intentar dar de baja un producto que no existe
    @Test
    void givenSkuInexistente_whenOcultarProducto_thenThrowRecursoNoEncontradoException() {
        // GIVEN
        Long sku = 999L; // sku fantasma

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(null); // mock reporta celda vacia

        // THEN
        assertThrows(RecursoNoEncontradoException.class, () -> service.ocultarProducto(sku)); // se valida excepcion esperada
        verify(productoRepository, never()).save(any()); // se asegura que no hubo alteracion en bd
    }

    // Test para verificar la reactivacion de visibilidad de un producto
    @Test
    void givenSkuValido_whenActivarProducto_thenSetActivoTrueYGuardar() {
        // GIVEN
        Long sku = 100L; // sku a dar de alta
        Producto producto = new Producto(); // se crea modelo bd
        producto.setActivo(false); // se inicia apagado

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(producto); // mock localiza el registro inactivo

        service.activarProducto(sku); // se ejecuta alta logica

        // THEN
        assertTrue(producto.isActivo()); // se valida alteracion del flag a activo
        verify(productoRepository, times(1)).save(producto); // se confirma actualizacion en bd
    }

    // Test para bloquear activacion por sku nulo
    @Test
    void givenSkuNulo_whenActivarProducto_thenThrowRecursoNuloException() {
        // GIVEN
        Long sku = null; // parametro nulo

        // WHEN AND THEN
        assertThrows(RecursoNuloException.class, () -> service.activarProducto(sku)); // se aborta por nulidad
        verify(productoRepository, never()).findBySku(any()); // resguardo seguro
    }

    // Test para verificar error al intentar activar un producto inexistente
    @Test
    void givenSkuInexistente_whenActivarProducto_thenThrowRecursoNoEncontradoException() {
        // GIVEN
        Long sku = 999L; // sku fantasma

        // WHEN
        when(productoRepository.findBySku(sku)).thenReturn(null); // mock reporta no encontrado

        // THEN
        assertThrows(RecursoNoEncontradoException.class, () -> service.activarProducto(sku)); // se detiene operacion por id invalido
        verify(productoRepository, never()).save(any()); // se protege persistencia
    }

    // Test para comprobar sincronizacion orquestada de estado en true
    @Test
    void givenSkuValidoYActivoTrue_whenCambiarEstadoVisibilidad_thenSetActivoTrueYGuardar() {
        // GIVEN
        Long sku = 100L; // sku notificado
        boolean activo = true; // orden de activacion recibida de inventario
        Producto producto = new Producto(); // modelo catalogo
        producto.setActivo(false); // estado inicial apagado

        // WHEN
        when(productoRepository.findById(sku)).thenReturn(Optional.of(producto)); // mock encuentra producto por id optional

        service.cambiarEstadoVisibilidad(sku, activo); // se procesa orden remota

        // THEN
        assertTrue(producto.isActivo()); // flag modificado a verdadero con exito
        verify(productoRepository, times(1)).save(producto); // actualizacion resguardada en catalogo
    }

    // Test para comprobar sincronizacion orquestada de estado en false
    @Test
    void givenSkuValidoYActivoFalse_whenCambiarEstadoVisibilidad_thenSetActivoFalseYGuardar() {
        // GIVEN
        Long sku = 100L; // sku notificado
        boolean activo = false; // orden de desactivacion por quiebre de stock
        Producto producto = new Producto(); // modelo catalogo
        producto.setActivo(true); // estado inicial encendido

        // WHEN
        when(productoRepository.findById(sku)).thenReturn(Optional.of(producto)); // mock encuentra producto

        service.cambiarEstadoVisibilidad(sku, activo); // se procesa orden remota

        // THEN
        assertFalse(producto.isActivo()); // flag modificado a inactivo correctamente
        verify(productoRepository, times(1)).save(producto); // actualizacion sincronizada masivamente
    }

    // Test para verificar interrupcion por descalce de sincronizacion (producto inexistente en catalogo)
    @Test
    void givenSkuNoExistenteEnCatalogo_whenCambiarEstadoVisibilidad_thenThrowRuntimeException() {
        // GIVEN
        Long sku = 999L; // sku huerfano enviado por cola o feign
        boolean activo = true; // flag orden

        // WHEN
        when(productoRepository.findById(sku)).thenReturn(Optional.empty()); // mock gatilla ausencia

        // THEN
        assertThrows(RuntimeException.class, () -> service.cambiarEstadoVisibilidad(sku, activo)); // se captura error critico de consistencia
        verify(productoRepository, never()).save(any()); // se bloquea impacto destructivo en bd
    }
}
