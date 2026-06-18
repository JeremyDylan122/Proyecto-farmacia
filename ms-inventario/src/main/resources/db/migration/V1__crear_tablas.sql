CREATE SCHEMA IF NOT EXISTS esquema_inventario

CREATE TABLE IF NOT EXISTS esquema_inventario.inventario_stock (
    sku BIGINT PRIMARY KEY,
    cantidad_total INT NOT NULL,
    ubicacion_bodega VARCHAR(100) NOT NULL
);

CREATE TABLE NOT EXISTS esquema_inventario.lote (
    codigo_lote VARCHAR(50) PRIMARY KEY,
    cantidad_lote INT NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    id_orden_compra BIGINT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    sku BIGINT,
    CONSTRAINT fk_lote_stock FOREIGN KEY (sku) REFERENCES inventario_stock(sku)
);

CREATE TABLE NOT EXISTS esquema_inventario.movimiento_inventario (
    id_movimiento BIGSERIAL PRIMARY KEY,
    codigo_lote VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL,
    referencia_id BIGINT NOT NULL,
    fecha_hora TIMESTAMP NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    sku BIGINT,
    CONSTRAINT fk_movimiento_stock FOREIGN KEY (sku) REFERENCES inventario_stock(sku) ON DELETE SET NULL
);