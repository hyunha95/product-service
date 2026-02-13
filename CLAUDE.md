# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Product Microservice** - Spring Boot backend service for managing product-related operations.

- **Port**: 9090
- **Database**: PostgreSQL (Docker Compose)
- **Java Version**: 21
- **Spring Boot**: 3.5.10

## Quick Start

```bash
# Start PostgreSQL
docker compose up -d

# Run application
./gradlew bootRun

# Run tests
./gradlew test

# Run specific test class
./gradlew test --tests "kr.co.haulic.product.*"

# Stop PostgreSQL
docker compose down
```

**Always use `./gradlew` (wrapper) instead of system Gradle.**

## Project Structure

```
src/main/java/kr/co/haulic/product/
└── ProductApplication.java    # Main application class

src/main/resources/
├── application.yaml           # Server config (port 9090)
├── static/                    # Static resources
└── templates/                 # Templates (if using server-side rendering)
```

## Configuration

**application.yaml:**
- Server port: 9090
- Application name: product

**Docker Compose:**
- PostgreSQL database
- Database: mydatabase
- User: myuser
- Password: secret
- Port: 5432

## Development Guidelines

### Architecture
- Follow DDD patterns used in the `haulic/` project (if implementing similar bounded contexts)
- Use four-layer structure: presentation, application, domain, infrastructure
- Keep business logic in domain layer

### Code Style
- Java conventions: `PascalCase` classes, `camelCase` methods/fields
- Descriptive suffixes: `*Controller`, `*UseCase`, `*Service`, `*Repository`, `*Exception`
- Lombok: `@Getter`, `@Builder`, `@NoArgsConstructor`/`@AllArgsConstructor`

### Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- Lombok
- Spring Boot DevTools
- Spring Boot Docker Compose Support

## Integration with Other Services

This service is part of the haulic-vibecode workspace:
- **haulic** (port 18080) - Main CMS backend
- **haulic-cms-frontend** (port 3000) - Admin dashboard
- **chunwon-market** (port 3000) - Customer storefront
- **product** (port 9090) - This microservice

See workspace root `CLAUDE.md` for overall architecture.

## Database

### Schema Management
When adding database migrations:
1. Use Flyway or Liquibase for version control
2. Place migrations in `src/main/resources/db/migration/`
3. Test migrations: `docker compose down -v && docker compose up -d`

### Connection
Default connection settings from compose.yaml:
- URL: `jdbc:postgresql://localhost:5432/mydatabase`
- Username: `myuser`
- Password: `secret`
