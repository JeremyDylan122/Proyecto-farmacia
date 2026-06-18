CREATE SCHEMA IF NOT EXISTS compras;

CREATE TABLE compras.factura (
    id_factura BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL,
    fecha_emision TIMESTAMPTZ NOT NULL DEFAULT NOW(), 
    rut_cliente VARCHAR(20) NOT NULL,
    nombre_cliente VARCHAR(100) NOT NULL,
    total_factura DECIMAL(10, 2) NOT NULL
);

CREATE TABLE compras.compra (
    id_orden_compra BIGSERIAL PRIMARY KEY,
    rut_proveedor VARCHAR(20) NOT NULL,
    sku BIGINT NOT NULL,
    cantidad INT NOT NULL,
    total_compra DECIMAL(10, 2) NOT NULL,
    id_factura BIGINT UNIQUE, 
    
    CONSTRAINT fk_compra_factura FOREIGN KEY (id_factura) REFERENCES compras.factura(id_factura) ON DELETE SET NULL
);