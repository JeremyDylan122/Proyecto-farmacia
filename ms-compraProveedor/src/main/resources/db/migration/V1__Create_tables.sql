CREATE SCHEMA IF NOT EXISTS compras;

CREATE TABLE IF NOT EXISTS compras.factura (
    id_factura BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    fecha_emision TIMESTAMP NOT NULL,
    rut_cliente VARCHAR(20) NOT NULL,
    nombre_cliente VARCHAR(100) NOT NULL,
    total_factura DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS compras.compra (
    id_orden_compra VARCHAR(36) PRIMARY KEY,
    rut_proveedor VARCHAR(20) NOT NULL,
    sku BIGINT NOT NULL,
    cantidad BIGINT NOT NULL,
    codigo_lote VARCHAR(255) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    total_compra DECIMAL(10, 2) NOT NULL,
    id_factura BIGINT UNIQUE,
    CONSTRAINT fk_compra_factura FOREIGN KEY (id_factura) REFERENCES compras.factura(id_factura) ON DELETE SET NULL
);