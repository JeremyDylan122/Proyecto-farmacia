package com.producto.mscatalogo.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final TipoRecetaRepository tipoRecetaRepository;
    private final ProductoDTOMapper productoDTOMapper;

    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll() 
                .stream()                   
                .map(productoDTOMapper::toDTO) 
                .toList();                  
    }

    // BUSCAR POR SKU
    public ProductoDTO buscarPorSku(Long sku){
        if (sku == null) {
            throw new RecursoNuloException("Debe ingresar un SKU para buscar el producto.");
        }
        Producto producto = productoRepository.findBySku(sku);
        if (producto == null){
            throw new RecursoNoEncontradoException("Sku: " + sku + " no encontrado.");
        }

        return productoDTOMapper.toDTO(producto);
    }

    // LISTAR POR CATEGORIA
    public List<ProductoDTO> listarPorCategoria(String cat){
        if (!categoriaRepository.existsByNombreIgnoreCase(cat)) {
            throw new RecursoNoEncontradoException("La categoria no existe.");
        }
        List<Producto> productos = productoRepository.findByCategoriaNombreIgnoreCase(cat);
        return productos.stream()
                .map(productoDTOMapper::toDTO)
                .toList();
    }

    // AGREGAR PRODUCTO
    public ProductoDTO agregarProducto(ProductoDTO productoDTO){
        if (productoRepository.existsBySku(productoDTO.getSku())){
            throw new RecursoDuplicadoException("Ya existe un producto con ese SKU.");
        }
        Categoria categoria = categoriaRepository.findByNombreIgnoreCase(productoDTO.getCategoria());
        if (categoria == null){
            throw new RecursoNoEncontradoException("La categoria no existe.");
        }
        TipoReceta tipoReceta = tipoRecetaRepository.findByNombreIgnoreCase(productoDTO.getTipoReceta());
        if (tipoReceta == null){
            throw new RecursoNoEncontradoException("El tipo de receta no existe.");
        }
        Producto producto = productoDTOMapper.toModel(productoDTO);
        producto.setCategoria(categoria);
        producto.setTipoReceta(tipoReceta);

        Producto productoGuardado = productoRepository.save(producto);
        return productoDTOMapper.toDTO(productoGuardado);
    }
    
    // ACTUALIZAR PRODUCTO
    public ProductoDTO actualizarProducto(Long sku, ProductoDTO dto) {
        if (sku == null) {
            throw new RecursoNuloException("Debe ingresar un SKU para actualizar el producto.");
        }
        Producto producto = productoRepository.findBySku(sku);
        if (producto==null) {
            throw new RecursoNoEncontradoException("Para actualizar debe ingresar un SKU.");   
        }
    
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setLaboratorio(dto.getLaboratorio());
        producto.setActivo(dto.isActivo());

        Categoria categoria = categoriaRepository.findByNombreIgnoreCase(dto.getCategoria());
        if (categoria == null){
            throw new RecursoNoEncontradoException("La categoria no existe.");
        }
        TipoReceta receta = tipoRecetaRepository.findByNombreIgnoreCase(dto.getTipoReceta());
        if (receta == null){
            throw new RecursoNoEncontradoException("El tipo de receta no existe.");
        }
        producto.setCategoria(categoria);
        producto.setTipoReceta(receta);

        Producto productoActualizado = productoRepository.save(producto);
        return productoDTOMapper.toDTO(productoActualizado);
    }

    // OCULTAR PRODUCTO
    public void ocultarProducto(Long sku){
        if (sku == null) {
            throw new RecursoNuloException("Debe ingresar un SKU para borrar el producto.");
        }
        Producto producto = productoRepository.findBySku(sku);
        if (producto == null) {
            throw new RecursoNoEncontradoException("No existe producto con SKU: " + sku);
        }
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    // ACTIVAR PRODUCTO
    public void activarProducto(Long sku){
        if (sku == null) {
            throw new RecursoNuloException("Debe ingresar un SKU para activar el producto.");
        }
        Producto producto = productoRepository.findBySku(sku);
        if (producto == null) {
            throw new RecursoNoEncontradoException("No existe producto con SKU: " + sku);
        }
        producto.setActivo(true);
        productoRepository.save(producto);
    }


    public void cambiarEstadoVisibilidad(Long sku, boolean activo) {
        // 1. Buscamos el producto en la base de datos de catálogo
        Producto producto = productoRepository.findById(sku)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado en catálogo con SKU: " + sku));
        
        // 2. Cambiamos su estado según lo que nos ordenó Inventario
        producto.setActivo(activo);
        
        // 3. Guardamos los cambios
        productoRepository.save(producto);
    }

}
