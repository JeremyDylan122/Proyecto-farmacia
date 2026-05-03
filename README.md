# Microservicio Proveedor

API REST para la gestión de proveedores del sistema Farmacia - Proyecto Duoc UC

### *Stack Tecnológico*
- *Backend*: Java 17 + Spring Boot 3.5.6
- *Base de datos*: MySQL + Spring Data JPA
- *Build*: Maven
- *Arquitectura*: Microservicio REST

### *Endpoints principales*
| Método | Endpoint | Descripción |
| --- | --- | --- |
| GET | /api/proveedores | Lista todos los proveedores |
| GET | /api/proveedores/{id} | Busca proveedor por ID |
| POST | /api/proveedores | Crea un nuevo proveedor |
| PUT | /api/proveedores/{id} | Actualiza un proveedor |
| DELETE | /api/proveedores/{id} | Elimina un proveedor |

### *Ejecutar en local*
1. Clonar el repo
```bash
git clone https://github.com/JonathanRiveraG/Proveedor.git
