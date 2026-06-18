
CREATE TABLE IF NOT EXISTS cliente_beneficio_bd.cliente (
    run VARCHAR(8) PRIMARY KEY,
    dv VARCHAR(1) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    correo VARCHAR(80) NOT NULL,
    telefono VARCHAR(9)NOT NULL,
    id_beneficio BIGINT,
    CONSTRAINT fk_cliente_beneficio
        FOREIGN KEY (id_beneficio)
        REFERENCES cliente_beneficio_bd.beneficio(id)
);