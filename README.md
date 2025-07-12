# 🧩 Plantilla Backend Java · Spring Boot

Este proyecto es una plantilla **completa y profesional** para construir APIs REST seguras y escalables con **Java 21 y Spring Boot 3**.

Fue desarrollada como base sólida para futuros proyectos y demuestra dominio de buenas prácticas de desarrollo backend, seguridad, arquitectura por capas, testing, documentación y más.

---

## 💼 Tecnologías principales

- **Java 21** + **Spring Boot 3.2**
- **Spring Security** con autenticación vía JWT
- **Spring Data JPA** + **Hibernate 6**
- **Flyway** para migraciones automáticas de base de datos
- **PostgreSQL** como base de datos principal
- **Docker Compose** para entorno local
- **Swagger / OpenAPI** para documentación automática
- **JUnit 5**, **Mockito** y pruebas de integración

---

## ✅ Funcionalidades implementadas

🔐 **Seguridad:**
- Login y protección de endpoints con JWT
- Configuración personalizada de filtros de seguridad

📄 **Documentación técnica:**
- Swagger UI en `/swagger-ui.html` con anotaciones OpenAPI

📦 **Persistencia:**
- PostgreSQL + JPA + migraciones Flyway
- Entidades auditadas (createdAt, updatedAt)

🧪 **Testing:**
- Unit tests para servicios y controladores
- Tests de integración sobre controladores reales

📂 **Arquitectura limpia:**
- Separación clara en capas: Controller – Service – Repository – DTO – Mapper
- Excepciones personalizadas y manejo global con `@ControllerAdvice`

⚙️ **Extras:**
- Paginación y ordenamiento con `Pageable`
- Docker para facilitar entorno local
- DevTools para desarrollo en caliente

---

## 👨‍💻 Objetivo del proyecto

> Diseñar una plantilla base **lista para producción** que pueda usarse como punto de partida en cualquier aplicación empresarial basada en Spring Boot.  
> Al mismo tiempo, mostrar experiencia sólida en desarrollo backend Java moderno.

---

## 🚀 Cómo ejecutar el proyecto

Requisitos:
- Java 21
- Docker
- Maven Wrapper (`./mvnw`)

Pasos:

```bash
# 1. Clonar el proyecto
git clone https://github.com/AlePrimo/Plantilla-Backend.git
cd Plantilla-Backend

# 2. Levantar base de datos
docker-compose up -d

# 3. Ejecutar backend
./mvnw spring-boot:run

# 4. Acceder a Swagger UI
http://localhost:8080/swagger-ui.html
