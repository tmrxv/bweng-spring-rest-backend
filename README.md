# Time Capsule Web Application Backend

This is the backend for the **Time Capsule** web application, built with **Spring Boot** and **Java 17**. It provides RESTful APIs for user authentication, user management, and managing time capsule posts. The backend uses **Spring Security** with JWT for authentication, **Spring Data JPA** for database access, and integrates **Swagger** for API documentation.

---

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Setup and Running](#setup-and-running)
- [Environment Variables](#environment-variables)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Quick Start](#quick-start)
- [License](#license)

---

## Features

- User registration and login with JWT authentication
- Role-based access control (`USER`, `ADMIN`)
- CRUD operations for time capsule posts
- Pagination for user listing
- API documentation with Swagger
- Validation for all incoming requests
- Password hashing with BCrypt

---

## Technology Stack

- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- Spring Security (JWT)
- H2 / PostgreSQL / MySQL (configurable)
- Swagger (OpenAPI)
- Maven

---

## Project Structure
src/main/java
│
├── config/ # Configuration classes (Swagger, Security, Beans)
├── controller/ # REST API controllers
├── dto/ # Data Transfer Objects for requests/responses
├── entity/ # JPA entities representing database tables
├── repository/ # Spring Data repositories
├── security/ # JWT filter and security logic
├── service/ # Business logic and service layer
└── SpringRestBackendApplication.java


### Layers Explained

1. **Entities:** Map to database tables using JPA.
2. **Repositories:** Handle database operations using Spring Data.
3. **Services:** Business logic, DTO mapping, validation.
4. **Controllers:** Define REST endpoints, request handling.
5. **DTOs:** Encapsulate request/response payloads, validation.
6. **Security:** JWT authentication and role-based authorization.
7. **Config:** Beans, password encoders, Swagger setup.

---

## Setup and Running

### Prerequisites

- Java 17
- Maven
- Database
- Optional: Postman for API testing

### Steps

1. Clone the repository:

```bash
git clone https://github.com/yourusername/time-capsule-backend.git
cd time-capsule-backend
```

2. Set environment variables in .env or system:
JWT_SECRET=your_secret_key
JWT_EXPIRATION_MS=3600000
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/time_capsule
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=yourpassword

3. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

## Environment Variables
| Variable                     | Description                         |
| ---------------------------- | ----------------------------------- |
| `JWT_SECRET`                 | Secret key used to sign JWT tokens  |
| `JWT_EXPIRATION_MS`          | JWT expiration time in milliseconds |
| `SPRING_DATASOURCE_URL`      | JDBC URL of the database            |
| `SPRING_DATASOURCE_USERNAME` | DB username                         |
| `SPRING_DATASOURCE_PASSWORD` | DB password                         |

## API Documentation
Swagger UI is available at: http://localhost:8080/swagger-ui/index.html
OpenAPI documentation is exposed at: http://localhost:8080/v3/api-docs

## Authentication
JWT-based authentication

Login endpoint: /api/auth/login
Register endpoint: /api/auth/register

Include JWT in Authorization header as:
Authorization: Bearer <token>

## Quick Start
Register a user
```bash
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","username":"user1","password":"securepassword"}'
```

Login to get JWT
```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","password":"securepassword"}'
```

Create a Time Capsule Post
```bash
curl -X POST http://localhost:8080/api/posts \
-H "Authorization: Bearer <JWT_TOKEN>" \
-H "Content-Type: application/json" \
-d '{"userId":1,"title":"My Future Self","message":"Hello!","sendAt":"2025-12-31T00:00:00"}'
```

Postman is highly recommended as a tool to test the API.

# License
This project is licensed under the MIT License.