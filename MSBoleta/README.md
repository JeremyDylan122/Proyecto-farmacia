# msBoleta

Microservicio Spring Boot para emitir y anular boletas, asociarlas a clientes, validar recetas y recalcular descuentos segun el beneficio del cliente.
Las integraciones HTTP con otros microservicios estan implementadas con OpenFeign.

## Funcionalidad incluida

- Crear boletas con uno o mas productos.
- Consultar una boleta por ID.
- Consultar boletas por RUN de cliente.
- Consultar boletas donde exista un SKU especifico.
- Editar productos de una boleta.
- Anular boletas por ID.
- Registrar recetas por cliente.
- Consultar y desactivar recetas de cliente.

## Dependencias externas esperadas

Este microservicio queda listo para integrarse con:

- `msClienteBeneficio`
  - `GET /api/clientes/{run}`
  - `GET /api/beneficios/{id}/descuento`
- `msGestionInventario`
  - `GET /api/productos/{sku}`

## Variables de entorno

- `SUPABASE_DB_URL`
- `SUPABASE_DB_USERNAME`
- `SUPABASE_DB_PASSWORD`
- `CLIENTE_BENEFICIO_API_URL`
- `INVENTARIO_API_URL`

## Schema preparado

El proyecto esta configurado para trabajar con el schema:

- `boleta_service_bd`

## Endpoints principales

- `POST /api/boletas`
- `GET /api/boletas/{id}`
- `GET /api/boletas/cliente/{run}`
- `GET /api/boletas/producto/{sku}`
- `PUT /api/boletas/{id}/productos`
- `PATCH /api/boletas/{id}/anular`
- `POST /api/recetas-clientes`
- `GET /api/recetas-clientes/{id}`
- `GET /api/recetas-clientes/cliente/{run}`
- `PATCH /api/recetas-clientes/{id}/desactivar`

## Ejemplo de creacion de boleta

```json
{
  "runCliente": "12345678",
  "productos": [
    { "sku": 780004, "cantidad": 1 },
    { "sku": 780017, "cantidad": 2 }
  ]
}
```
