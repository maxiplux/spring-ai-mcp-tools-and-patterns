# Spring Boot 3.4 Application Development Prompt

## Overview

Create a complete Spring Boot 3.4 application with the following components:

- Entity classes: Product, Category, and User with roles
- Repositories for each entity
- Service layer implementations
- RESTful controllers

## Entity Requirements

### Product Entity

- ID (auto-generated)
- Name
- Description
- Price
- SKU (unique identifier)
- Creation date
- Last modified date
- Relationship with Category (Many-to-One)

### Category Entity

- ID (auto-generated)
- Name
- Description
- Creation date
- Last modified date
- Relationship with Products (One-to-Many)

### User Entity

- ID (auto-generated)
- Username (unique)
- Email (unique)
- Password (encrypted)
- Creation date
- Last modified date
- User roles (USER, ADMIN)

## Repository Requirements

- Create JpaRepository interfaces for each entity
- Include custom query methods for common operations:
    - Find products by category
    - Find products by price range
    - Find users by role
    - Find categories by name (case insensitive)

## Service Layer Requirements

- Create service interfaces and implementations for each entity
- Implement CRUD operations
- Add business logic for:
    - Product inventory management
    - User registration with role assignment
    - Category management

## Controller Requirements

- RESTful controllers for each entity with proper endpoints
- Implement GET, POST, PUT, DELETE operations

- Include API documentation using Swagger/OpenAPI
- Implement proper error handling and response status codes

## Additional Notes

- Use Java 17+ features where appropriate
- Implement validation for all entities
- Follow Spring Boot best practices for project structure
- Use Spring Data JPA specifications for complex queries
- Include proper exception handling
- Provide unit and integration tests

## Output Format

Please generate the complete code structure including:

1. Entity classes with annotations
2. Repository interfaces with custom methods
3. Service interfaces and implementations
4. Controller classes with endpoint mappings
5. Any additional configuration classes needed