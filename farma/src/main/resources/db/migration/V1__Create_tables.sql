CREATE TABLE compra (
    id_orden_compra BIGSERIAL PRIMARY KEY,
    rut_proveedor VARCHAR(20) NOT NULL,
    sku BIGINT NOT NULL,
    cantidad INT NOT NULL,
    total_compra DECIMAL(10, 2) NOT NULL
);

