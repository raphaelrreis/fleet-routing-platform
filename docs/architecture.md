# Fleet Routing Platform Architecture

## Objective

Build a road transportation decision-support platform that ingests telemetry, identifies shipment risk, and proposes explainable route changes. A deterministic routing engine calculates geographic decisions; the LLM orchestrates tools and explains the resulting recommendation.

## Principles

1. **Azure only:** all managed production services belong to the Azure ecosystem.
2. **Infrastructure as Code:** no permanent resource is created manually in the portal; Terraform is the source of truth.
3. **Reliable messaging:** Azure Service Bus carries business commands and workflow events.
4. **Telemetry is not a command:** a later phase routes high-frequency signals through IoT Hub and Event Hubs.
5. **The LLM does not calculate routes:** Azure Maps and a deterministic solver calculate cost, constraints, and route geometry.
6. **Human in the loop:** the MVP requires human approval before an operational route change.
7. **Passwordless first:** production workloads use Managed Identity and RBAC.
8. **Cell based:** each Azure cell serves a bounded fleet set and fails independently.

## Cell topology

The term *cell* is synonymous with *deployment stamp*: a complete, repeatable unit with bounded capacity.

```text
                         Control Plane
                   fleetId -> cellId / capacity
                              |
              +---------------+---------------+
              |                               |
              v                               v
        Cell brs-01                     Cell eus2-01
   +-------------------+            +-------------------+
   | Spring Boot APIs  |            | Spring Boot APIs  |
   | Service Bus       |            | Service Bus       |
   | Route Worker      |            | Route Worker      |
   | Operational Data  |            | Operational Data  |
   | Managed Identity  |            | Managed Identity  |
   +-------------------+            +-------------------+
```

The control plane does not participate in route processing. It resolves the cell that owns a fleet, tracks capacity, and coordinates cell lifecycle. After routing, every critical operation remains inside the selected cell.

In code, `CellId` is required message context. In Azure, each cell receives its own resource group and Service Bus namespace. A single Terraform module definition produces every cell to minimize configuration drift.

## System context

```text
Truck / Simulator
        |
        v
Telemetry API (Spring Boot)
        |
        | RouteRiskDetected
        v
Azure Service Bus
        |
        v
Route Planning Worker (Spring Boot)
        |             |
        |             +--> Azure Maps / solver
        |
        +----------------> Spring AI / Azure OpenAI
                              |
                              v
                    Structured Recommendation
                              |
                              v
                        Human Approval
```

## Initial events

| Event | Producer | Consumer | Purpose |
|---|---|---|---|
| `TelemetryReceived` | Telemetry API | Risk Detector | Record a normalized telemetry reading |
| `RouteRiskDetected` | Risk Detector | Route Planning Worker | Request route risk analysis |
| `ReplanningRequested` | Route Planning Worker | Route Engine | Calculate deterministic alternatives |
| `RouteProposed` | Fleet Routing Platform | Operations API | Present a recommendation to the operator |
| `RouteApproved` | Operations API | Dispatch Adapter | Authorize an operational route change |

## Service Bus

The baseline uses:

- a `logistics-events` topic with duplicate detection;
- a `route-planning` subscription with PeekLock, bounded delivery attempts, and a DLQ;
- a `route-replanning-commands` queue for targeted commands;
- a `MessageId` derived from the business process identifier;
- idempotent consumers because PeekLock provides *at-least-once* delivery.

## Official sources

- [Azure Deployment Stamps as independent cells](https://learn.microsoft.com/en-us/azure/architecture/patterns/deployment-stamp)
- [Azure Service Bus isolation in multitenant systems](https://learn.microsoft.com/en-us/azure/architecture/guide/multitenant/service/service-bus)
- [Azure Well-Architected Framework for Service Bus](https://learn.microsoft.com/en-us/azure/well-architected/service-guides/azure-service-bus)
- [Prevent message loss and duplicate processing](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-message-loss-and-duplicates)
- [Queues, topics, and subscriptions](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-queues-topics-subscriptions)
- [Dead-letter queues](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-dead-letter-queues)
- [Azure Maps truck routing](https://learn.microsoft.com/en-us/rest/api/maps/route/post-route-directions?view=rest-maps-2025-01-01)
- [Spring AI tool calling](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [OpenAI Docs for Microsoft Azure OpenAI](https://developers.openai.com/api/reference/ruby#microsoft-azure-openai)
- [AzureRM Terraform Provider for Service Bus](https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/resources/servicebus_namespace)
