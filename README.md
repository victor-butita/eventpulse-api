# EventPulse API

Backend API for creating and booking event tickets — a lightweight Eventbrite-style service.
Built with **Spring Boot 3 / Kotlin / Java 17**, MariaDB, Liquibase, Docker, CircleCI, and SonarQube Cloud.

Repo: [victor-butita/eventpulse-api](https://github.com/victor-butita/eventpulse-api)

## Phase 0 status

| Deliverable | Status |
|-------------|--------|
| Maven Kotlin Spring Boot scaffold | Done |
| Dockerfile + docker-compose (app + MariaDB) | Done |
| Liquibase baseline changelog | Done |
| CircleCI build-test-scan pipeline | Done |
| SonarQube Cloud (via CircleCI) | Needs CircleCI env vars |
| Swagger / OpenAPI (springdoc) | Done |
| CircleCI required status check on `main` | Victor: enable in GitHub branch protection |

## Prerequisites

- JDK 17+
- Maven 3.9+
- Docker + Docker Compose

## Run locally (Docker)

```bash
docker compose up --build
```

- API: http://localhost:8080
- Health: http://localhost:8080/api/v1/health
- Swagger UI: http://localhost:8080/swagger-ui.html

Liquibase applies changelogs automatically on startup.

## Run tests

```bash
mvn clean verify
```

Tests use an in-memory H2 database (`test` profile). JaCoCo enforces **≥ 80% line coverage** on `verify` (report: `target/site/jacoco/index.html`).

## Project layout

```
src/main/kotlin/com/eventpulse/api/
  controller/   # HTTP endpoints
  service/      # business logic (Phase 1+)
  repository/   # persistence (Phase 1+)
  dto/          # request/response models (Phase 1+)
  entity/       # JPA entities (Phase 1+)
  config/       # OpenAPI and other config
```

## CircleCI + SonarQube Cloud

Add these **CircleCI project environment variables** (Project Settings → Environment Variables):

| Variable | Purpose |
|----------|---------|
| `SONAR_TOKEN` | SonarQube Cloud token |
| `SONAR_PROJECT_KEY` | e.g. from Sonar project settings |
| `SONAR_ORGANIZATION` | your Sonar org key |

Pipeline job: `build-test-scan` (`mvn clean verify` then `mvn sonar:sonar`).

### Branch protection (Victor)

On `main`, require:

1. Pull request before merge + 1 approval  
2. Status check: CircleCI `build-test-scan`  
3. Conversation resolution  

## Git workflow

- Branch from `main`: `feature/<issue-number>-short-description`
- Conventional commits (`feat:`, `fix:`, `chore:`, …)
- One feature = one PR; squash-merge after CI + review

## Config template

See `src/main/resources/application-example.yml`. Never commit `.env` or real DB credentials.
