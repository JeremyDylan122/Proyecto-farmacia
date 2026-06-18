CREATE INDEX IF NOT EXISTS idx_boleta_cliente_run
    ON boleta_service_bd.boleta (cliente_run);

CREATE INDEX IF NOT EXISTS idx_boleta_detalle_sku
    ON boleta_service_bd.boleta_detalle (sku_producto);

CREATE INDEX IF NOT EXISTS idx_receta_cliente_run
    ON boleta_service_bd.receta_cliente (run_cliente);
