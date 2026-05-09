CREATE SCHEMA IF NOT EXISTS bd_proveedor;

CREATE TABLE IF NOT EXISTS bd_proveedor.proveedores (
    rut_proveedor VARCHAR(15) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    direccion VARCHAR(100) NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE

);

INSERT INTO bd_proveedor.proveedores (rut_proveedor, nombre, direccion, telefono, email) 
VALUES ('16.555.789-6', 'Insumo Farmacia', 'Calle 123, Viña del Mar', '987654321', 'servicios@insofarm.cl');
ON CONFLICT (rut_proveedor) DO NOTHING;
INSERT INTO bd_proveedor.proveedores (rut_proveedor, nombre, direccion, telefono, email) 
VALUES ('98.765.432-1', 'Primer farma Centro', 'Avenida 456, Valparaíso', '993456789', 'CentroFar@oficina.cl');
ON CONFLICT (rut_proveedor) DO NOTHING;
INSERT INTO bd_proveedor.proveedores (rut_proveedor, nombre, direccion, telefono, email) 
VALUES ('55.555.555-5', 'InsuMed', 'Plaza 789, Santiago', '978655555', 'insumed@insumed.cl');
ON CONFLICT (rut_proveedor) DO NOTHING;