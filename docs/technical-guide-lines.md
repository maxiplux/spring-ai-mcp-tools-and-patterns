# B2B Commerce Project Guide

## Build/Test Commands

- Build project: `./gradlew build`
- Run tests: `./gradlew test`
- Single test: `./gradlew test --tests "app.quantun.b2b.TestClassName.testMethodName"`
- Skip tests: `./gradlew build -x test`
- Run app: `./gradlew bootRun`
- Docker build: `docker build -t b2bcommerce .` or `docker-compose up -d`

## Code Style Guidelines

- Java 17, Spring Boot 3.4.x
- We are always working with application.properties
- All exeptions must follow the specification problemdetails
- You cannot return in controller methods List<*> All needs to be a Page<*> of PagingAndSortingRepository
- You need to work with DTOs and Mappers, never with entities in controllers directly
- There are two types of DTOs: Request and Response
- When you are writting controllers and they are restcontrollers you need add doccumentation with OpenAPI(springdoc-openapi)
- Request DTOs are used for creating or updating entities, while Response DTOs are used for returning data to the client.
- Use ModelMapper for mapping between DTOs and entities.
- Use @Valid for validating request DTOs.
- Use @Validated for validating service methods.
- Use @Transactional for service methods that modify data.
- Use @Cacheable for caching service methods that return data.
- Use @CacheEvict for evicting cache entries when data is modified.
- Use @CachePut for updating cache entries when data is modified.
- 
- You need to work always with @RequiredArgsConstructor to inject dependencies
- All repositories must have CrudRepository and PagingAndSortingRepository
- Max line length: 120 characters
- Indentation: 4 spaces, no tabs
- Naming: camelCase for variables/methods, PascalCase for classes, UPPER_SNAKE_CASE for constants
- Use Jakarta EE (jakarta.*) not legacy javax.* imports
- No wildcard imports or unused imports
- Follow existing patterns for entity classes, services, controllers
- Use JPA annotations consistently (@Entity, @Column, etc.)
- Error handling: Use custom exceptions or ProblemDetail for standardized error responses
- Organize imports alphabetically
- Entity classes should include proper equals/hashCode using entity ID
- Document public methods with Javadoc
- Use Lombok annotations consistently (@Getter, @Setter, etc.)

## Technical Decisions

### Technology Stack

- **Java 17**: Chosen for its long-term support and modern language features.
- **Spring Boot 3.4.x**: Provides a comprehensive framework for building enterprise applications with minimal configuration.
- **Hibernate**: Used for ORM to simplify database interactions.
- **Liquibase**: Manages database schema changes in a controlled manner.
- **Docker**: Containerizes the application for consistent deployment across environments.



### Configuration Choices

- **application.properties**: Centralized configuration file for managing application settings.
- **ModelMapper**: Simplifies object mapping between DTOs and entities.
- **OpenAPI**: Generates API documentation for better developer experience.
- **GlobalExceptionHandler**: Standardizes error handling across the application.

### Dependency Management

- **Gradle**: Chosen for its flexibility and performance in managing project dependencies and build tasks.
- **Spring Boot Starters**: Simplifies dependency management by providing pre-configured sets of dependencies for common use cases.
- **JUnit**: Used for unit testing to ensure code quality and reliability.
- **Mockito**: Facilitates mocking in tests to isolate components and test behavior.
human-readable format.

### Design Patterns

- **Singleton**: Ensures a single instance of certain classes, such as configuration classes.
- **Factory**: Used to create instances of complex objects, such as AWS clients.
- **Builder**: Simplifies the creation of complex objects, such as entities with many fields.
- **Strategy**: Encapsulates algorithms, such as different authentication strategies, to make them interchangeable.

### Best Practices

- **Code Reviews**: Regular code reviews to maintain code quality and share knowledge among team members.
- **Continuous Integration**: Automated builds and tests to catch issues early in the development process.
- **Documentation**: Comprehensive documentation to ensure that the codebase is understandable and maintainable.
- **Security**: Regular security audits and updates to protect against vulnerabilities.
### Summary
- When you finish a task you need to write all your decisions in the README.md explaining why you made this decision and what is the impact of this decision in the project.