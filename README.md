# Bookstore

Projekt zaliczeniowy — system zarządzania księgarnią/wypożyczalnią książek:
rejestracja i logowanie użytkowników, przeglądanie i wyszukiwanie książek,
rezerwacje oraz historia wypożyczeń i zwrotów.

## Stos technologiczny
- Java 17, Spring Boot 3.5
- Spring Web, Spring Data JPA (Hibernate), Spring Security (RBAC: `USER` / `ADMIN`)
- PostgreSQL + Flyway (migracje)
- springdoc-openapi (Swagger UI)
- Maven, Docker
- JUnit 5 + JaCoCo (pokrycie ≥ 80%)

## Uruchomienie lokalne
```bash
# 1. baza danych
docker compose up -d db

# 2. aplikacja
mvn spring-boot:run
```
- API / Swagger UI: http://localhost:8080/swagger-ui.html
- Specyfikacja OpenAPI: http://localhost:8080/v3/api-docs

## Testy i pokrycie
```bash
mvn verify
# raport JaCoCo: target/site/jacoco/index.html
```

## Diagram ERD
    AUTHORS | BOOKS : pisze
    USERS   | LOANS : wypozycza
    BOOKS   | LOANS : dotyczy

    AUTHORS { bigint id PK
              varchar first_name
              varchar last_name }
    USERS   { bigint id PK
              varchar username UK
              varchar email UK
              varchar role
              boolean enabled }
    BOOKS   { bigint id PK
              varchar book_type
              varchar title
              varchar isbn UK
              bigint author_id FK
              numeric price
              int available_copies }
    LOANS   { bigint id PK
              bigint user_id FK
              bigint book_id FK
              varchar status
              numeric penalty }
