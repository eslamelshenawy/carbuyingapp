# Car Buying App

A Spring Boot application for managing car buying requests and supplier offers with integrated vehicle inspection services.

## Tech Stack

- Java 17
- Spring Boot 3.2.x
- Spring Data JPA
- Flyway (database migrations)
- H2 (development) / PostgreSQL (production)
- SpringDoc OpenAPI (Swagger UI)
- Lombok
- Jakarta Bean Validation
- Docker & Docker Compose

## Prerequisites

- JDK 17+
- Maven 3.8+

## Build & Run

```bash
# Build the project
mvn clean package

# Run the application (uses H2 in-memory database by default)
mvn spring-boot:run

# Run tests
mvn test
```

The application starts on `http://localhost:8080`.

## API Documentation

Swagger UI is available at: `http://localhost:8080/swagger-ui.html`

OpenAPI spec: `http://localhost:8080/v3/api-docs`

## H2 Console (Development)

Available at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:carbuyingdb`
- Username: `sa`
- Password: *(empty)*

## API Endpoints

### Customer Requests

| Method | Endpoint                      | Description           |
|--------|-------------------------------|-----------------------|
| POST   | `/api/requests`               | Create a new request  |
| GET    | `/api/requests`               | List requests (paginated, optional `status` filter) |
| GET    | `/api/requests/{id}`          | Get a single request  |
| PATCH  | `/api/requests/{id}/status`   | Update request status |

### Supplier Offers

| Method | Endpoint                              | Description                    |
|--------|---------------------------------------|--------------------------------|
| POST   | `/api/requests/{requestId}/offers`    | Submit an offer for a request  |
| GET    | `/api/requests/{requestId}/offers`    | List offers for a request (paginated) |
| GET    | `/api/suppliers/{supplierId}/offers`  | List offers by supplier (paginated) |

## Docker

```bash
# Run with PostgreSQL using Docker Compose
docker-compose up --build
```

This starts both the application and a PostgreSQL database. The app will be accessible at `http://localhost:8080`.

## Architecture

```
com.carbuyingapp
├── controller/        REST API endpoints
├── service/           Business logic
├── repository/        Data access layer (Spring Data JPA)
├── model/
│   ├── entity/        JPA entities
│   ├── dto/           Request/Response DTOs
│   └── enums/         Status and company enums
├── inspection/        Strategy pattern for inspection companies
│   ├── InspectionService          (interface)
│   ├── InspectionServiceFactory   (factory / resolver)
│   ├── AutoCheckInspectionService (AUTO_CHECK_CO implementation)
│   └── VehiVerifyInspectionService(VEHI_VERIFY_INC implementation)
└── exception/         Global exception handling
```

### Design Highlights

- **Strategy Pattern**: Inspection companies are implemented as interchangeable strategies resolved at runtime via `InspectionServiceFactory`. Adding a new company requires only a new `InspectionService` implementation — no existing code changes needed (Open/Closed Principle).
- **State Machine**: Request status transitions are validated (PENDING → ACTIVE/CANCELLED, ACTIVE → CLOSED/CANCELLED).
- **Scalability**: Database indexes on frequently queried columns. Unique constraint prevents duplicate offers per supplier per request.
