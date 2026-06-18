# MSboleta (Microservicio de Gestión de Boletas y Recetas Médicas)

Este microservicio se encarga del ciclo de vida de la facturación mediante la emisión, consulta y anulación de boletas de venta. Asimismo, gestiona el registro y validación de recetas médicas de clientes requeridas para la venta de medicamentos controlados.

---

## 🏗️ Arquitectura y Tecnologías
*   **Java 17** y **Spring Boot 3.x**
*   **Spring Data JPA** para el acceso a datos.
*   **Base de Datos**: PostgreSQL alojado en Supabase (esquema `boleta_service_bd`).
*   **Spring Cloud OpenFeign** para la comunicación REST declarativa con microservicios externos.
*   **Flyway** para el control de versiones y migraciones del esquema de base de datos.
*   **Lombok** para reducir el código repetitivo (*boilerplate*).
*   **Jakarta Validation** para asegurar la calidad de los datos de entrada en las solicitudes.

---

## ⚙️ Configuración y Despliegue Local
*   **Puerto por defecto**: `8084`
*   **Dependencias externas**:
    *   **MSclienteBeneficio**: Corre en el puerto `8083` (para consultar datos de clientes y sus porcentajes de descuento).
    *   **msGestionInventario**: Corre en el puerto `8081` (para consultar precios de productos, nombres y verificar si requieren receta).

---

## 📡 Endpoints de la API

### 1. Gestión de Boletas (`/api/boletas`)
*   `POST /api/boletas`: Emite una nueva boleta de venta. Realiza las siguientes validaciones:
    *   Verifica la existencia del cliente y su porcentaje de descuento llamando a `MSclienteBeneficio`.
    *   Consulta los precios y la necesidad de receta médica de cada producto llamando a `msGestionInventario`.
    *   Si algún producto requiere receta médica, valida que el cliente tenga una receta vigente y activa del tipo correspondiente en la base de datos de `MSboleta`.
    *   Calcula el Neto, el IVA (19%), aplica el beneficio de descuento y calcula el monto Bruto.
*   `GET /api/boletas/{id}`: Obtiene el detalle estructurado de una boleta por su ID de base de datos.
*   `GET /api/boletas/cliente/{run}`: Devuelve el listado histórico de boletas emitidas a un RUN de cliente específico.
*   `GET /api/boletas/producto/{sku}`: Lista todas las boletas que incluyan la compra de un producto (SKU) en particular.
*   `PUT /api/boletas/{id}/productos`: Permite modificar la cantidad de productos en una boleta activa, recalculando los subtotales e impuestos correspondientes.
*   `PATCH /api/boletas/{id}/anular`: Cambia el estado de una boleta a anulada. Una vez anulada, la boleta no puede ser modificada.

### 2. Gestión de Recetas (`/api/recetas-clientes`)
*   `POST /api/recetas-clientes`: Registra una nueva receta médica para un cliente.
*   `GET /api/recetas-clientes/{id}`: Obtiene una receta médica específica por su ID.
*   `GET /api/recetas-clientes/cliente/{run}`: Obtiene la lista completa de recetas asociadas a un RUN de cliente.
*   `PATCH /api/recetas-clientes/{id}/desactivar`: Inactiva manualmente una receta de forma anticipada.

---

## 📁 Estructura de Base de Datos (Tablas principales)
1.  `boleta`: Cabecera de la boleta (folio, fecha de emisión, montos netos, IVA, descuentos y estado de anulación).
2.  `boleta_detalle`: Detalle de productos asociados a la boleta (SKU, nombre, cantidad, precio unitario y monto de línea).
3.  `cliente_snapshot`: Copia histórica de los datos del cliente al momento de la emisión para mantener la integridad de los registros contables.
4.  `receta_cliente`: Registro de recetas médicas autorizadas para la compra de fármacos controlados.
