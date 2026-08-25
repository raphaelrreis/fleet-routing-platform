# Fleet Routing Platform

A reference implementation for exploring Spring Boot, Spring AI, and Azure services in an event-driven transportation and logistics system.

The platform incrementally processes vehicle telemetry, queries deterministic routing services, and produces explainable recommendations for a human operator.

## Project status

- [x] Project bootstrap
- [x] Architecture decisions and Service Bus Terraform baseline
- [x] Fleet, shipment, and telemetry domain model
- [x] Route risk detection
- [ ] Asynchronous orchestration
- [ ] Azure Maps integration
- [ ] Spring AI and Azure OpenAI integration
- [ ] Observability and resilience

## Running locally

### Supported toolchain

- Java 25 LTS (`25.0.4-tem` through SDKMAN)
- Maven 3.9.16
- Spring Boot 4.1.1
- Spring Framework 7.0.9, managed by Spring Boot
- Spring AI 2.0.1

Activate the project-pinned versions:

```bash
sdk env
```

Maven Enforcer fails the build when it runs outside the Java 25 release family or with a Maven version earlier than 3.9.16.

```bash
mvn spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

## Documentation

- [Architecture](docs/architecture.md)
- [ADRs](docs/adr)
- [LinkedIn article draft](docs/linkedin-article-draft.md)
- [Terraform infrastructure](infra/terraform)
