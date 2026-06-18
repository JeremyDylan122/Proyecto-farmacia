package com.inventario.msinventario.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.msinventario.dto.loteDTOs.LoteDTO;
import com.inventario.msinventario.dto.loteDTOs.LoteDTOMapper;
import com.inventario.msinventario.exceptions.RecursoNuloException;
import com.inventario.msinventario.model.Lote;
import com.inventario.msinventario.repository.LoteRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;
    private final LoteDTOMapper loteDTOMapper;

    //CREAR LOTE
    @Transactional
    public LoteDTO crearLote (LoteDTO loteDTO){
        // 1. VALIDACIÓN DE LA LLAVE PRIMARIA (String codigoLote)
        if (loteDTO.getCodigoLote() == null || loteDTO.getCodigoLote().trim().isEmpty()) {
            throw new RecursoNuloException("Error: El código de lote es obligatorio.");
        }

        // Verificamos en el LoteRepository si ya existe este código de lote
        if (loteRepository.existsById(loteDTO.getCodigoLote())) {
            throw new RuntimeException("Error: El lote con el código '" + loteDTO.getCodigoLote() + "' ya está registrado.");
        }

        // REGLA DE NEGOCIO: No se reciben productos con menos de 1 año (365 días) de vida útil
        LocalDate fechaMinimaPermitida = LocalDate.now().plusYears(1);
        if (loteDTO.getFechaVencimiento().isBefore(fechaMinimaPermitida)) {
            throw new RuntimeException("Error: Rechazo de mercadería. No se permiten lotes con una fecha de vencimiento menor a un año desde hoy (" + fechaMinimaPermitida + ").");
        }

        // 2. VALIDACIÓN DE LA REFERENCIA (Long stock / SKU)
        if (loteDTO.getSku() == null) {
            throw new RecursoNuloException("Error: El código de Stock (SKU) asociado es obligatorio.");
        }


        // 3. TRANSFORMACIÓN Y PERSISTENCIA
        Lote nuevoLote = loteDTOMapper.toModel(loteDTO);
        Lote loteGuardado = loteRepository.save(nuevoLote);
        log.info("Lote guardado con éxito. ID Asignado: {}", loteGuardado.getCodigoLote());
        // 4. RESPUESTA
        return loteDTOMapper.toDTO(loteGuardado);
    }


    // BUSCAR LOTE POR ID
    @Transactional(readOnly = true)
    public LoteDTO buscarPorId(String codigoLote) {
        // Validamos que el parámetro no venga vacío
        if (codigoLote == null || codigoLote.trim().isEmpty()) {
            throw new RuntimeException("Error: El código de lote es obligatorio para realizar la búsqueda.");
        }
        log.info("Buscando lote en inventario con código: {}", codigoLote);
        Lote lote = loteRepository.findById(codigoLote)
                .orElseThrow(() -> new RuntimeException("Error: No se encontró ningún lote con el código: " + codigoLote));

        log.info("Lote {} encontrado con éxito. SKU asociado: {}", lote.getCodigoLote(), 
                 lote.getStock() != null ? lote.getStock().getSku() : "Ninguno");

        return loteDTOMapper.toDTO(lote);
    }


    //LISTAR TODOS LOS LOTES
    @Transactional(readOnly = true)
    public List<LoteDTO> listarTodos() {
        log.info("Solicitando el listado maestro de todos los lotes en existencia.");

        List<Lote> lotes = loteRepository.findAll();

        return lotes.stream()
                .map(loteDTOMapper::toDTO)
                .toList();
    }

    // LOGICA PARA DESCONTAR PRIMERO LO QUE VENCE PRIMERO
    @Transactional
    public String descontarLotesPorFEFO(Long sku, Integer cantidadAVender) {
        log.info("Aplicando estrategia FEFO en lotes para descontar {} unidades del SKU {}", cantidadAVender, sku);
        
        // 1. Obtener los lotes ordenados por vencimiento
        List<Lote> lotesDisponibles = loteRepository.findByStockSkuAndActivoTrueOrderByFechaVencimientoAsc(sku);

        if (lotesDisponibles.isEmpty()) {
            throw new RuntimeException("Error: No se encontraron lotes activos disponibles para el SKU: " + sku);
        }

        // Guardamos el código del primer lote para el historial de movimientos antes de alterar la lista
        String primerCodigoLoteAfectado = lotesDisponibles.get(0).getCodigoLote();
        int cantidadDescontar = cantidadAVender;

        // 2. Algoritmo de descuento secuencial
        for (Lote lote : lotesDisponibles) {
            if (lote.getCantidad() >= cantidadDescontar) {
                lote.setCantidad(lote.getCantidad() - cantidadDescontar);
                if (lote.getCantidad() == 0) {
                    lote.setActivo(false);
                }
                cantidadDescontar = 0;
            } else {
                cantidadDescontar -= lote.getCantidad();
                lote.setCantidad(0);
                lote.setActivo(false);
            }
            
            if (cantidadDescontar <= 0) {
                break;
            }
        }

        // 3. Guardar todos los lotes alterados de forma masiva
        loteRepository.saveAll(lotesDisponibles);
        log.info("Lotes actualizados con éxito según estrategia FEFO para el SKU {}", sku);
        
        return primerCodigoLoteAfectado;
    }

}
