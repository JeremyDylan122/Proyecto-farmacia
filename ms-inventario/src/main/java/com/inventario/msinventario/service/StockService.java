package com.inventario.msinventario.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.msinventario.dto.stockDTOs.StockDTO;
import com.inventario.msinventario.dto.stockDTOs.StockDTOMapper;
import com.inventario.msinventario.exceptions.RecursoNuloException;
import com.inventario.msinventario.exceptions.StockInsuficienteException;
import com.inventario.msinventario.model.Stock;
import com.inventario.msinventario.repository.StockRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockDTOMapper stockDTOMapper;

    @Transactional
    public StockDTO acumularOInicializarStock(Long sku, Integer cantidadEntrante) {
        if (sku == null || cantidadEntrante == null) {
            throw new RecursoNuloException("Error: El SKU y la cantidad entrante son obligatorios.");
        }
        if (cantidadEntrante < 1) {
            throw new RuntimeException("Error: La cantidad a acumular en el stock debe ser mayor o igual a 1.");
        }

        log.info("Procesando actualización de stock para SKU: {}. Cantidad entrante: {}", sku, cantidadEntrante);
        Stock stock = stockRepository.findById(sku).orElse(null);

        if (stock == null) {
            // CASO A: El producto es nuevo en la bodega, se inicializa desde cero
            log.warn("SKU {} no encontrado. Creando nuevo registro en bodega (RECEPCION).", sku);
            stock = new Stock();
            stock.setSku(sku);
            stock.setCantidadTotal(cantidadEntrante); // Nace directamente con la cantidad de la compra
            stock.setUbicacionBodega("RECEPCION");
        } else {
            // CASO B: El producto ya existía, se acumula la nueva mercadería
            log.info("SKU {} encontrado. Stock anterior: {}. Sumando nuevo ingreso.", sku, stock.getCantidadTotal());
            stock.setCantidadTotal(stock.getCantidadTotal() + cantidadEntrante);
        }

        Stock stockGuardado = stockRepository.save(stock);
        log.info("Stock para SKU {} actualizado con éxito. Nuevo total: {}", sku, stockGuardado.getCantidadTotal());

        return stockDTOMapper.toDTO(stockGuardado);
    }

    // LISTAR POR SKU
    @Transactional(readOnly = true)
    public StockDTO buscarPorSku(Long sku) {
        // 1. Validar que el parámetro no venga vacío
        if (sku == null) {
            throw new RecursoNuloException("Error: El SKU a buscar no puede ser nulo.");
        }

        Stock stock = stockRepository.findById(sku)
                .orElseThrow(() -> new RuntimeException(
                        "Error: El producto con SKU " + sku + " no está registrado en el sistema."));

        return stockDTOMapper.toDTO(stock);
    }

    // LISTAR TODOS
    @Transactional(readOnly = true)
    public List<StockDTO> listarStock() {
        log.info("Solicitando la lista completa de stock en inventario.");

        List<Stock> listaStock = stockRepository.findAll();

        return listaStock.stream()
                .map(stockDTOMapper::toDTO)
                .toList();
    }

    // DESCONTAR STOCK AL REALIZAR UNA VENTA
    @Transactional
    public StockDTO descontarStockPorVenta(Long sku, Integer cantidadAVender) {
        if (sku == null || cantidadAVender == null) {
            throw new RecursoNuloException("Error: El SKU y la cantidad a vender son obligatorios.");
        }

        log.info("Procesando descuento de stock por venta para el SKU: {}. Cantidad: {}", sku, cantidadAVender);
        
        // 1. Buscar el stock actual
        Stock stock = stockRepository.findById(sku)
                .orElseThrow(() -> new RecursoNuloException("Venta rechazada: El producto con SKU " + sku + " no existe en inventario."));

        // 2. Validar disponibilidad
        if (stock.getCantidadTotal() < cantidadAVender) {
            log.warn("Venta rechazada para SKU {}: Stock actual ({}) es menor a la cantidad solicitada ({}).",
                    sku, stock.getCantidadTotal(), cantidadAVender);
            throw new StockInsuficienteException("No se puede realizar la venta, no hay stock suficiente");
        }

        // 3. Restar matemáticamente del balance global
        stock.setCantidadTotal(stock.getCantidadTotal() - cantidadAVender);
        Stock stockGuardado = stockRepository.save(stock);
        
        log.info("Stock global descontado con éxito para SKU {}. Nuevo total: {}", sku, stockGuardado.getCantidadTotal());
        return stockDTOMapper.toDTO(stockGuardado);
    }

}
