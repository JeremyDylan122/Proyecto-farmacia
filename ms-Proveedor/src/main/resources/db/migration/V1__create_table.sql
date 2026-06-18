CREATE SCHEMA IF NOT EXISTS esquema_proveedor;

CREATE TABLE IF NOT EXISTS proveedor (
    rut_proveedor VARCHAR(15) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    direccion VARCHAR(100) NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);
