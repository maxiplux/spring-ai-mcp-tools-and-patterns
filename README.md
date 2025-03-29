# Spring AI MCP Catalog API

This project is a Spring Boot application that demonstrates the integration of Spring AI with a catalog management
system.

## Features

- Catalog management with categories and products
- User management with role-based access control
- RESTful API endpoints for all operations
- API documentation with OpenAPI 3.0 (Swagger)
- In-memory H2 database for easy testing
- Integration with Spring AI MCP (Model Composition Platform)

## Technologies

- Java 21
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- Spring Validation
- H2 Database
- SpringDoc OpenAPI (Swagger)
- Lombok
- Spring AI

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle or Maven

### Running the Application

1. Clone the repository
2. Set your Anthropic API key in the application.yml file or as an environment variable (`ANTHROPIC_API_KEY`)
3. Build and run the application:

```bash
./gradlew bootRun
```

The application will start at `http://localhost:8080`

### Accessing the API Documentation

Once the application is running, you can access the API documentation at:

```
http://localhost:8080/swagger-ui.html
```

### Accessing the H2 Console

The H2 in-memory database console is available at:

```
http://localhost:8080/h2-console
```

Use the following credentials:

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Initial Data

On startup, the application loads some sample data:

- Categories: Electronics, Clothing, Books
- Products: Several products in each category
- Users:
    - Admin: username: `admin`, password: `admin123`
    - Regular user: username: `user`, password: `user123`

## API Endpoints

The API provides the following main endpoints:

- `/api/categories`: Category management
- `/api/products`: Product management
- `/api/users`: User management (admin access required)
- `/api/auth`: Authentication and registration

## License

This project is licensed under the Apache License 2.0 