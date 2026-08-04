# 🔐 Auth Service — Microservicio de Autenticación & JWT

Este microservicio se encarga de la gestión de identidades, emitiendo y validando **JSON Web Tokens (JWT)** para la arquitectura distribuida del Sistema de Solicitudes de Soporte Técnico.

---

## 🚀 Tecnologías

- **Java 21 / Spring Boot 3.4.x**
- **Spring Security 6** (Autenticación Stateless)
- **JJWT 0.12.5** (Generación y firma de tokens JWT con HMAC SHA-256)
- **Spring Data JPA & Hibernate** (ORM)
- **MySQL 8.0**
- **Lombok**
- **Docker & Docker Compose**

---

## ⚡ Guía de Inicio Rápido (Local)

### Requisitos Previos

- JDK 21 o superior
- MySQL 8.0 ejecutándose en el puerto `3306` (o en puerto custom configurado)

### 1. Variables de Entorno / `application.properties`

Asegúrate de que `auth-service/src/main/resources/application.properties` apunte a tu base de datos:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/solicitudes_auth?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=2605

jwt.secret=java_spring_boot_micro_servicio_auth_security_2026_project_solicitudes
jwt.expiration=86400000
```

### 2. Ejecutar la aplicación

```bash
# En Windows (PowerShell / CMD)
.\mvnw.cmd spring-boot:run

# En Linux / macOS
./mvnw spring-boot:run
```

El servicio estará disponible en **`http://localhost:8081`**.

---

## 🐳 Despliegue con Docker

### Construir la imagen Docker
```bash
docker build -t auth-service:latest .
```

### Ejecutar con Docker Compose (Raíz del proyecto)
```bash
docker compose up -d auth-service
```

---

## 📄 Documentación Técnica

Para consultar la especificación completa de la arquitectura de seguridad, esquemas de BD y endpoints, consulta [AUTHDOCUMENTATION.md](./AUTHDOCUMENTATION.md).
