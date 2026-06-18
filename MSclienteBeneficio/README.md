# MSclienteBeneficio (Microservicio de Gestión de Clientes y Beneficios)

Este microservicio se encarga del registro, actualización y eliminación de los clientes de la cadena, así como de la administración de los diferentes niveles de beneficios de descuento asociados a cada uno de ellos. Es consumido principalmente por el microservicio de boletas para calcular descuentos financieros aplicables en compras.

---

## 🏗️ Arquitectura y Tecnologías
*   **Java 17** y **Spring Boot 3.x**
*   **Spring Data JPA** para el mapeo objeto-relacional y persistencia.
*   **Base de Datos**: PostgreSQL alojado en Supabase (esquema `cliente_beneficio_bd`).
*   **Flyway** para control de versiones y migraciones del esquema de base de datos.
*   **Lombok** para reducir código repetitivo.
*   **Jakarta Validation** para validar formatos de datos obligatorios en la API (ej: formato de correo, RUN del cliente).

---

## ⚙️ Configuración y Despliegue Local
*   **Puerto por defecto**: `8083`
*   **Esquema de Base de Datos**: `cliente_beneficio_bd`

---

## 📡 Endpoints de la API

### 1. Gestión de Clientes (`/api/clientes`)
*   `GET /api/clientes/{run}`: Obtiene los datos detallados de un cliente a partir de su RUN. Retorna también el porcentaje de descuento y los datos de su beneficio asociado.
*   `POST /api/clientes`: Registra un nuevo cliente en el sistema. Realiza validaciones sobre la obligatoriedad y formato del RUN, DV, correo, nombre y apellido.
*   `PUT /api/clientes/{run}`: Actualiza la información personal de un cliente existente (como nombre, correo o el beneficio asignado).
*   `DELETE /api/clientes/{run}/{dv}`: Elimina físicamente a un cliente de la base de datos a partir de su RUN y dígito verificador.

### 2. Gestión de Beneficios (`/api/beneficios`)
*   `GET /api/beneficios/{id}`: Obtiene el detalle completo del beneficio (nombre, descripción, porcentaje de descuento).
*   `GET /api/beneficios/{id}/descuento`: Retorna únicamente el valor entero que representa el porcentaje de descuento (ej: `15` para un 15% de descuento) asociado al identificador de beneficio provisto.

---

## 📁 Estructura de Base de Datos (Tablas principales)
1.  `clientes`: Registro de usuarios identificados por su RUN y dígito verificador. Posee una relación de llave foránea hacia la tabla de beneficios.
2.  `beneficio`: Catálogo de convenios o beneficios disponibles que definen las tasas de descuento porcentual aplicables.
