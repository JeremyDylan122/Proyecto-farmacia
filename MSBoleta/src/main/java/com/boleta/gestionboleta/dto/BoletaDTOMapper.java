package com.boleta.gestionboleta.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.boleta.gestionboleta.model.Boleta;
import com.boleta.gestionboleta.model.BoletaDetalle;
import com.boleta.gestionboleta.model.ClienteSnapshot;

@Component
public class BoletaDTOMapper {

    public BoletaDTO toDTO(Boleta boleta) {
        if (boleta == null) {
            return null;
        }

        BoletaDTO dto = new BoletaDTO();
        dto.setId(boleta.getId());
        dto.setFolio(boleta.getFolio());
        dto.setCliente(toClienteDTO(boleta.getCliente()));
        dto.setProductos(toDetalleDTOList(boleta.getProductos()));
        dto.setPorcentajeDescuento(boleta.getPorcentajeDescuento());
        dto.setMontoDescuento(boleta.getMontoDescuento());
        dto.setMontoNeto(boleta.getMontoNeto());
        dto.setIva(boleta.getIva());
        dto.setMontoBruto(boleta.getMontoBruto());
        dto.setFechaEmision(boleta.getFechaEmision());
        dto.setAnulada(boleta.isAnulada());
        return dto;
    }

    private ClienteBoletaDTO toClienteDTO(ClienteSnapshot cliente) {
        if (cliente == null) {
            return null;
        }

        ClienteBoletaDTO dto = new ClienteBoletaDTO();
        dto.setRun(cliente.getRun());
        dto.setDv(cliente.getDv());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setCorreo(cliente.getCorreo());
        dto.setIdBeneficio(cliente.getIdBeneficio());
        dto.setDescuentoEntero(cliente.getDescuentoEntero());
        return dto;
    }

    private List<BoletaDetalleDTO> toDetalleDTOList(List<BoletaDetalle> detalles) {
        return detalles.stream()
                .map(this::toDetalleDTO)
                .toList();
    }

    private BoletaDetalleDTO toDetalleDTO(BoletaDetalle detalle) {
        BoletaDetalleDTO dto = new BoletaDetalleDTO();
        dto.setSkuProducto(detalle.getSkuProducto());
        dto.setNombreProducto(detalle.getNombreProducto());
        dto.setTipoReceta(detalle.getTipoReceta());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setMontoLinea(detalle.getMontoLinea());
        return dto;
    }
}
