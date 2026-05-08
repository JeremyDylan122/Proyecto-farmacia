TRUNCATE TABLE cliente_beneficio_bd.cliente RESTART IDENTITY;

INSERT INTO cliente_beneficio_bd.beneficio (nombre, descuento) VALUES
('Fonasa', 8),
('Colmena', 15),
('Vida tres', 10),
('Nueva mas vida', 12);


INSERT INTO cliente_beneficio_bd.cliente (run, dv, nombre, apellido, correo, telefono, id_beneficio) VALUES
('12345678', 'k', 'Juan', 'Perez', 'juan.perez@example.com', '111111111', 1),
('87654321', '1', 'Maria', 'Gonzalez', 'maria.gonzalez@example.com', '222222222', 2),
('55555555', '0', 'Pedro', 'Ramirez', 'pedro.ramirez@example.com', '333333333', 3),
('44444444', '2', 'Ana', 'Lopez', 'ana.lopez@example.com', '444444444', 4);

