# 📖 Especificación Técnica: Auth Service (Microservicio de Autenticación)

---

## 1. Visión General y Arquitectura

El **Auth Service** actúa como el **Identity Provider (IdP)** central de la aplicación. Su función principal es validar las credenciales de los usuarios y firmar tokens **JWT** utilizando una clave secreta compartida con los servidores de recursos (Monolito).

```
                      +-------------------+
                      |   Cliente Frontend |
                      +---------+---------+
                                |
                   POST /login  | (Recibe JWT Token)
                                v
                      +-------------------+
                      |   Auth Service    |  <--- Emite JWT
                      |   (Puerto 8081)   |
                      +---------+---------+
                                |
         Sincronización Interna | (Crear / Editar / Borrar Usuarios)
                                v
                      +-------------------+
                      |     Monolito      |  <--- Valida JWT
                      |   (Puerto 8080)   |
                      +-------------------+
```

---

## 2. Modelo de Datos (`solicitudes_auth`)

### Tabla: `usuarios`

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Identificador único del usuario |
| `email` | `VARCHAR(255)` | `NOT NULL`, `UNIQUE` | Correo electrónico de inicio de sesión |
| `password` | `VARCHAR(255)` | `NOT NULL` | Contraseña encriptada con BCrypt |
| `rol` | `VARCHAR(50)` | `NOT NULL` | Rol asignado (`ROLE_ADMINISTRADOR`, `ROLE_TECNICO`, `ROLE_CLIENTE`) |
| `activo` | `BOOLEAN` | `DEFAULT true` | Estado de la cuenta de usuario |
| `fecha_creacion` | `DATETIME` | `NOT NULL` | Timestamp de creación |

---

## 3. Endpoints Públicos de Autenticación

### 3.1. Inicio de Sesión (Login)
- **Método:** `POST`
- **URL:** `/api/auth/login`
- **Body Request:**
```json
{
  "email": "admin@sistema.com",
  "password": "Admin123#"
}
```
- **Response Succesful (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@sistema.com",
  "rol": "ROLE_ADMINISTRADOR",
  "expiration": 86400000
}
```

### 3.2. Registro Directo de Usuario
- **Método:** `POST`
- **URL:** `/api/auth/register`
- **Body Request:**
```json
{
  "email": "nuevo@empresa.com",
  "password": "Password123#",
  "rol": "ROLE_CLIENTE"
}
```

---

## 4. Endpoints Internos para Comunicación entre Microservicios

Estos endpoints son utilizados por el Monolito (`:8080`) para auto-registrar, actualizar o desvincular cuentas de usuario cuando el Administrador opera desde la interfaz web.

### 4.1. Auto-registro Interno de Usuario
- **Método:** `POST`
- **URL:** `/api/auth/internal/crear-usuario`
- **Body Request:**
```json
{
  "email": "tecnico@empresa.com",
  "password": "TecnicoPass123#",
  "rol": "ROLE_TECNICO"
}
```

### 4.2. Actualización Interna de Perfil y/o Contraseña
- **Método:** `POST`
- **URL:** `/api/auth/internal/actualizar-usuario`
- **Body Request:**
```json
{
  "emailOriginal": "tecnico@empresa.com",
  "nuevoEmail": "tecnico_nuevo@empresa.com",
  "password": "NuevaPassword123#"
}
```

### 4.3. Eliminación Interna de Cuenta de Usuario
- **Método:** `DELETE`
- **URL:** `/api/auth/internal/eliminar-usuario?email={correo}`
- **Response (200 OK):** Confirma la eliminación física de la cuenta de usuario en `solicitudes_auth`.

---

## 5. Estructura de Claims del JWT

El token JWT generado incluye los siguientes datos en su payload:

```json
{
  "sub": "admin@sistema.com",
  "rol": "ROLE_ADMINISTRADOR",
  "iat": 1779156745,
  "exp": 1779243145
}
```

- **Firma:** Algoritmo HMAC SHA-256 (`HS256`) con el secreto compartido configurado en `JWT_SECRET`.
