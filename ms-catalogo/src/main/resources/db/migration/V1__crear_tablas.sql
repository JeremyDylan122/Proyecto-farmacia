CREATE SCHEMA IF NOT EXISTS esquema_catalogo

CREATE TABLE IF NOT EXISTS esquema_catalogo.categoria(
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR (50) NOT NULL
);

CREATE TABLE IF NOT EXISTS esquema_catalogo.tipo_receta (
    id_tipo_receta SERIAL PRIMARY KEY,
    nombre VARCHAR (50) NOT NULL UNIQUE,
    descripcion VARCHAR (255)
);

CREATE TABLE IF NOT EXISTS esquema_catalogo.producto (
    sku BIGINT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio DECIMAL(9,2) NOT NULL,
    laboratorio VARCHAR(50) NOT NULL,
    descripcion VARCHAR(250),
    id_tipo_receta INTEGER NOT NULL REFERENCES tipo_receta(id_tipo_receta),
    id_categoria INTEGER NOT NULL REFERENCES categoria(id_categoria)
);