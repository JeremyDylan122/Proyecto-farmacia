package com.producto.mscatalogo.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.producto.mscatalogo.dto.ProductoDTO;
import com.producto.mscatalogo.service.ProductoService;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc; // cliente para simular peticiones http en memoria

    @MockitoBean
    private ProductoService productoService; // mockbean para falsear la capa de negocio


    // Test para validar la peticion get que lista todo el catalogo
    @Test
    void givenProductosExistentes_whenObtenerTodos_thenReturnStatusOkYLista() throws Exception {
        // GIVEN
        ProductoDTO dto = new ProductoDTO(); // se crea dto simulado
        List<ProductoDTO> listaSimulada = List.of(dto); // se arma lista de respuesta

        // WHEN
        when(productoService.listarTodos()).thenReturn(listaSimulada); // mock retorna la lista en el servicio

        // THEN
        mockMvc.perform(get("/api/v1/productos")) // se ejecuta peticion get (ajusta la ruta base si aplica)
                .andExpect(status().isOk()) // se valida codigo de estado 200 ok
                .andExpect(jsonPath("$.size()").value(1)); // se valida que retorne un elemento json
        
        verify(productoService, times(1)).listarTodos(); // se audita invocacion unica al servicio
    }

    // Test para validar la busqueda exitosa de un producto especifico por url path
    @Test
    void givenSkuValido_whenObtenerPorSku_thenReturnStatusOkYProductoDTO() throws Exception {
        // GIVEN
        Long sku = 100L; // sku de prueba
        ProductoDTO dto = new ProductoDTO(); // dto vacio de respuesta
        dto.setSku(sku); // se setea el sku correspondiente

        // WHEN
        when(productoService.buscarPorSku(sku)).thenReturn(dto); // mock asocia la busqueda unitaria

        // THEN
        mockMvc.perform(get("/api/v1/productos/{sku}", sku)) // se ejecuta peticion con path variable
                .andExpect(status().isOk()) // se valida codigo 200 ok
                .andExpect(jsonPath("$.sku").value(sku)); // se valida correspondencia del sku en json
        
        verify(productoService, times(1)).buscarPorSku(sku); // se audita flujo hacia el servicio
    }

    // Test para validar el filtro de busqueda por segmento de categoria
    @Test
    void givenCategoriaValida_whenObtenerPorCategoria_thenReturnStatusOkYLista() throws Exception {
        // GIVEN
        String categoria = "FARMACIA"; // categoria filtro
        ProductoDTO dto = new ProductoDTO(); // dto muestra
        List<ProductoDTO> listaSimulada = List.of(dto); // lista envoltura

        // WHEN
        when(productoService.listarPorCategoria(categoria)).thenReturn(listaSimulada); // mock inyecta comportamiento al servicio

        // THEN
        mockMvc.perform(get("/api/v1/productos/categoria/{categoria}", categoria)) // se ejecuta peticion get con path variable texto
                .andExpect(status().isOk()) // se valida codigo HTTP 200
                .andExpect(jsonPath("$.size()").value(1)); // se corrobora la entrega del listado
        
        verify(productoService, times(1)).listarPorCategoria(categoria); // se verifica disparo del flujo
    }

    // Test para validar la baja logica por medio del verbo delete
    @Test
    void givenSkuValido_whenOcultarProducto_thenReturnStatusNoContent() throws Exception {
        // GIVEN
        Long sku = 100L; // sku a eliminar logicanente

        // WHEN
        doNothing().when(productoService).ocultarProducto(sku); // mock indica que metodo void no hara nada

        // THEN
        mockMvc.perform(delete("/api/v1/productos/{sku}", sku)) // se ejecuta llamada delete
                .andExpect(status().isNoContent()); // se valida respuesta 204 sin cuerpo
        
        verify(productoService, times(1)).ocultarProducto(sku); // se valida llamada unica al servicio void
    }

    // Test para validar la reactivacion usando patch mapping selectivo
    @Test
    void givenSkuValido_whenActivar_thenReturnStatusNoContent() throws Exception {
        // GIVEN
        Long sku = 100L; // sku objetivo de reactivacion

        // WHEN
        doNothing().when(productoService).activarProducto(sku); // mock asocia ejecucion silenciosa de void

        // THEN
        mockMvc.perform(patch("/api/v1/productos/{sku}/activar", sku)) // se ejecuta verbo patch mapeado
                .andExpect(status().isNoContent()); // se valida respuesta 204 no content
        
        verify(productoService, times(1)).activarProducto(sku); // se audita invocacion de flujo
    }

    // Test para verificar sincronizacion remota usando put mapping con request parameter
    @Test
    void givenSkuYParametroActivo_whenCambiarVisibilidad_thenReturnStatusOk() throws Exception {
        // GIVEN
        Long sku = 100L; // sku notificado
        boolean activo = true; // variable de visibilidad externa

        // WHEN
        doNothing().when(productoService).cambiarEstadoVisibilidad(sku, activo); // mock asocia ejecucion void para integracion

        // THEN
        mockMvc.perform(put("/api/v1/productos/{sku}/visible", sku) // se ejecuta llamada put
                        .param("activo", String.valueOf(activo))) // se inyecta request param (?activo=true)
                .andExpect(status().isOk()); // se valida respuesta 200 de exito vacio
        
        verify(productoService, times(1)).cambiarEstadoVisibilidad(sku, activo); // se comprueba ejecucion de sincronizacion
    }
}

