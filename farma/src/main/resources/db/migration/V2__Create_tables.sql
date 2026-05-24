CREATE SCHEMA IF NOT EXISTS compras;

CREATE TABLE IF NOT EXISTS compras.factura (
    id_factura BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL UNIQUE, 
    fecha_emision DATE NOT NULL,
    total_factura DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS compras.detalle (
    id_detalle BIGSERIAL PRIMARY KEY,
    id_factura BIGINT NOT NULL,
    sku BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    
    subtotal DECIMAL(10, 2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,
    
    CONSTRAINT fk_factura FOREIGN KEY (id_factura) REFERENCES compras.factura(id_factura) ON DELETE CASCADE
);