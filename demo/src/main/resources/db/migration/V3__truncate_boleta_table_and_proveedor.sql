TRUNCATE TABLE cliente_beneficio_bd.cliente RESTART IDENTITY;

INSERT INTO cliente_beneficio_bd.beneficio (nombre, descuento) VALUES
('Fonasa', 8),
('Colmena', 15),
('Vida tres', 10),
('Nueva mas vida', 12);


INSERT INTO cliente_beneficio_bd.cliente (run,dv, nombre, apellido, correo, telefono) VALUES
('12345678', 'k', 'Juan', 'Pérez', 'juan.perez@example.com', '111111111'),
('87654321', '1', 'María', 'González', 'maria.gonzalez@example.com', '222222222'),
('55555555', '0', 'Pedro', 'Ramírez', 'pedro.ramirez@example.com', '333333333'),
('44444444', '2', 'Ana', 'López', 'ana.lopez@example.com', '444444444');

