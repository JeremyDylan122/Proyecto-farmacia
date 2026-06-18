CREATE SCHEMA IF NOT EXISTS compras;

CREATE TABLE IF NOT EXISTS compras.detalle_factura (
    id_detalle_factura BIGSERIAL PRIMARY KEY,
    id_factura BIGINT NOT NULL,
    id_orden_compra VARCHAR(36) NOT NULL,
    codigo_lote BIGINT NOT NULL,
    cantidad INT NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    sub_total DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_factura FOREIGN KEY (id_factura) REFERENCES compras.factura(id_factura) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_compra FOREIGN KEY (id_orden_compra) REFERENCES compras.compra(id_orden_compra) ON DELETE CASCADE
);