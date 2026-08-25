# Spring Boot, AI, and Azure: From Fleet Telemetry to an Actionable Incident Recommendation

[Português (Brasil)](linkedin-article-draft.pt-BR.md)

> Draft. Every behavior described here is tied to executable code and automated tests in the repository.

A refrigerated shipment is already on the road when its cargo temperature rises above the agreed limit. Fuel is low and the latest telemetry indicates a 35-minute delay.

The useful question is not “Can an LLM calculate a route?” It should not. Distance, ETA, vehicle restrictions, and safety constraints belong to deterministic services that can be tested and audited.

The question I wanted this MVP to answer was more operational: once the platform has verified that a route is at risk, can AI turn those facts into a clear incident recommendation without inventing data?

That became the project’s main feature: **AI-assisted incident recommendation**.

## The feature, end to end

The implemented flow is deliberately explicit:

1. Deterministic rules evaluate canonical telemetry and detect route risk.
2. The command transaction stores the risk assessment and a transactional outbox event atomically in PostgreSQL.
3. An asynchronous worker claims `RouteRiskDetected` from the outbox.
4. Spring AI sends the verified assessment facts to Azure OpenAI.
5. The model must return a typed `RouteRecommendation`: `recommendation`, `rationale`, and `requiredActions`.
6. The CQRS query endpoint exposes the assessment, the reasons that triggered it, and the recommendation status.

If the AI call fails, the assessment is not lost. The outbox worker retries with exponential backoff and moves an exhausted event to a dead-letter state. The query side continues to show what happened instead of hiding the failure behind a timeout.

This boundary matters. AI interprets the incident and explains the next operational steps. It does not calculate routes, ETAs, distances, or safety constraints. Azure Maps and deterministic domain services will own those calculations.

## Why CQRS and a transactional outbox

Telemetry arrives quickly, while an AI response depends on a remote service with different latency and availability characteristics. Holding the HTTP transaction open across both concerns would couple ingestion to the model.

The command path therefore has a narrow responsibility: normalize the incoming payload, run the risk rules, and commit the assessment with its outbox event. Both writes succeed or neither does.

The asynchronous path generates the recommendation and updates the read model. Clients use a separate query endpoint to follow the status: `PENDING`, `COMPLETED`, or `FAILED`.

This is CQRS at a practical scale. It is not two databases or a large framework. It is a clear separation between changing state and reading the operational view.

## Protecting the domain from telemetry formats

The external telematics payload uses speed in meters per second, fuel as a ratio, and delay in seconds. The domain model uses kilometers per hour, percentage, and minutes.

An anti-corruption layer performs that translation before the risk rules run. Vendor naming and units stop at the integration boundary, which keeps the domain stable when a telematics provider changes.

## Kafka and Service Bus solve different problems

The platform uses the Kafka API for telemetry and domain-event streaming. Redpanda provides that contract locally; in Azure, Event Hubs exposes a Kafka-compatible endpoint.

Azure Service Bus remains the right fit for business commands and workflow coordination that need queues, sessions, explicit retries, and dead-letter queues. Treating both products as interchangeable would blur their operational strengths.

Microsoft documents the Kafka compatibility of Event Hubs and the message settlement behavior of Service Bus, including the possibility of redelivery. Consumers still need idempotency; a broker setting alone does not provide an end-to-end exactly-once guarantee.

Sources: [Event Hubs for Apache Kafka](https://learn.microsoft.com/en-us/azure/event-hubs/azure-event-hubs-kafka-overview) and [Service Bus message transfers, locks, and settlement](https://learn.microsoft.com/en-us/azure/service-bus-messaging/message-transfers-locks-settlement).

## A cell-based Azure target

The target runtime is divided into independent cells based on Azure’s Deployment Stamps pattern. Each cell contains its Spring Boot workloads, messaging resources, operational data, identity, and capacity limits. A fleet is assigned to one cell.

This limits the impact of a faulty release or an abnormal telemetry spike. It also permits progressive rollout: deploy to one cell, observe it, then continue. The cost is duplicated infrastructure and more deliberate routing and observability.

Source: [Azure Deployment Stamps pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/deployment-stamp).

## The multicloud part is a migration boundary

The client scenario starts with workloads in AWS and GCP and moves this capability to Azure. Terraform configures all three providers, but their roles are intentionally different:

- AWS and GCP are source environments for inventory, coexistence, replication, and cutover checks.
- Azure is the target runtime for every new platform component.

ECS, EKS, Cloud Run, and GKE workloads move toward AKS or Azure Container Apps. SQS, SNS, and Pub/Sub workflows map to Service Bus. MSK, Kinesis, and streaming-oriented Pub/Sub flows map to Event Hubs. PostgreSQL workloads move from RDS or Cloud SQL to Azure Database for PostgreSQL Flexible Server.

This is a transition, not a permanent active-active design. Once the Azure workload is stable and the cutover criteria are met, the source path is retired.

Source: [Azure Migration and Modernization Center](https://azure.microsoft.com/en-us/products/azure-migrate/).

## Running the MVP

The repository uses Java 25 LTS, Spring Boot 4.1, Spring AI 2.0, PostgreSQL, Kafka, Docker, Kubernetes, and Terraform.

Docker Compose starts PostgreSQL and Redpanda for local development. The application can run with a deterministic local recommendation adapter, so the full incident flow is testable without cloud credentials. Activating the `azure-ai` profile switches the recommendation port to Azure OpenAI. A Helm chart deploys the workload to AKS, and Terraform defines the repeatable Azure cell baseline.

The project is small enough to understand in one sitting, but the difficult boundaries are real: atomic state changes, asynchronous work, retries, typed AI output, CQRS, an anti-corruption layer, and a cloud migration path.

Repository: [github.com/raphaelrreis/fleet-routing-platform](https://github.com/raphaelrreis/fleet-routing-platform)

The next increment connects Azure Maps, adds end-to-end resilience tests, and provisions the managed runtime services for the first Azure cell.
