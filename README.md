# Fleet Routing Platform

[Português (Brasil)](README.pt-BR.md)

A functional MVP built with Spring Boot, Spring AI, and Azure services for an event-driven transportation and logistics system.

The platform incrementally processes vehicle telemetry, applies deterministic risk rules, and produces explainable incident recommendations for fleet operations teams.

## Primary AI feature

The MVP centers on **AI-assisted incident recommendation**:

1. Deterministic rules detect route risk from canonical telemetry.
2. A command transaction stores the assessment and an outbox event atomically.
3. An asynchronous worker reads `RouteRiskDetected`.
4. Spring AI sends only verified assessment facts to Azure OpenAI.
5. The model returns a typed `RouteRecommendation` with a recommendation, rationale, and required actions.
6. The CQRS query endpoint exposes assessment and recommendation status.

AI does not calculate routes, ETAs, distances, or safety constraints. Those decisions remain deterministic and auditable. See [AI-assisted incident recommendation](docs/ai-incident-recommendation.md).

## Project status

- [x] Project bootstrap
- [x] Architecture decisions and Service Bus Terraform baseline
- [x] Fleet, shipment, and telemetry domain model
- [x] Route risk detection
- [x] AWS and GCP migration context with Azure as the target
- [x] CQRS command and query paths
- [x] Transactional outbox and asynchronous orchestration
- [x] Spring AI and Azure OpenAI adapter
- [x] Kafka event publisher, Docker Compose, and AKS Helm chart
- [ ] Azure Maps integration
- [ ] Azure-managed observability and end-to-end resilience tests

## Runtime architecture

- **PostgreSQL:** command state, query projection, and transactional outbox.
- **Azure Service Bus:** business commands, workflow coordination, sessions, and DLQs.
- **Kafka API:** telemetry and domain-event streaming. Local development uses Redpanda; Azure uses Event Hubs through its Kafka-compatible endpoint.
- **Docker Compose:** PostgreSQL and Redpanda for local development.
- **Kubernetes:** Helm deployment for AKS, repeated per Azure cell.

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

Start the local infrastructure and application:

```bash
docker compose up -d postgres redpanda
SPRING_PROFILES_ACTIVE=kafka mvn spring-boot:run
```

### CQRS API

Submit the command:

```bash
curl -i http://localhost:8080/api/v1/route-risk-assessments \
  -H 'Content-Type: application/json' \
  -d '{
    "cellId": "brs-01",
    "shipment": {
      "shipmentId": "5aab0aa4-39d9-4e18-852b-2f4cc85f31b3",
      "weightKg": 8200,
      "requiresRefrigeration": true,
      "maximumCargoTemperatureCelsius": 5.0,
      "priority": "CRITICAL",
      "status": "IN_TRANSIT",
      "origin": {"latitude": -15.793889, "longitude": -47.882778},
      "destination": {"latitude": -16.686891, "longitude": -49.264794},
      "deliveryDeadline": "2026-08-25T01:00:00Z",
      "assignedTruckId": "c2a8f24d-30a4-4d52-af7d-f6d1e4bece31"
    },
    "telemetry": {
      "observedAt": "2026-08-24T22:30:00Z",
      "latitude": -16.020000,
      "longitude": -48.100000,
      "speedMetersPerSecond": 18.5,
      "fuelLevelRatio": 0.11,
      "cargoTemperatureCelsius": 8.4,
      "estimatedDelaySeconds": 2100
    }
  }'
```

Use the returned `Location` header to query the read model:

```bash
curl http://localhost:8080/api/v1/route-risk-assessments/{assessmentId}
```

## Documentation

- [Architecture](docs/architecture.md)
- [AI-assisted incident recommendation](docs/ai-incident-recommendation.md)
- [AWS and GCP migration to Azure](docs/cloud-migration.md)
- [ADRs](docs/adr)
- [Terraform infrastructure](infra/terraform)
