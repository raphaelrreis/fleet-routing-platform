# Spring Boot and AI on Azure: Building a Telemetry-Driven Fleet Routing Platform

> Work in progress. A section is considered complete only after the corresponding behavior exists in the repository and is covered by automated tests.

## Introduction

A refrigerated truck is carrying a time-sensitive shipment. The cargo temperature begins to rise, fuel is below the expected level, and traffic data indicates that the delivery window will be missed.

Which truck should take over the shipment? Which route respects the vehicle dimensions and cargo restrictions? How can the platform explain its recommendation without delegating a critical decision to a generative model?

This is the problem I chose to explore with Spring Boot, Spring AI, and Azure instead of building another generic chatbot.

The goal is not to ask an LLM to "invent the best route." The model interprets the incident, invokes typed tools, and explains an alternative calculated by deterministic components.

## The first decision: separate telemetry from workflow messaging

Not every message has the same operational requirements.

Location, speed, and temperature can generate thousands of continuous signals. Events such as `RouteRiskDetected` and `RouteProposed`, however, represent meaningful business state changes that require reliable delivery, retries, and a dead-letter queue.

For that reason, Azure Service Bus is the workflow messaging backbone. A later phase will introduce IoT Hub and Event Hubs for high-volume telemetry ingestion.

## Initial architecture

<!-- Add the architecture diagram after the first Azure adapters are implemented. -->

The initial flow is:

1. A Spring Boot API receives simulated telemetry.
2. Deterministic rules identify shipment risk.
3. The application publishes a `RouteRiskDetected` event to Service Bus.
4. A worker queries fleet availability, shipment constraints, and the route engine.
5. Spring AI uses those facts to produce a structured recommendation.
6. A human operator approves or rejects the proposed change.

## Why cell-based architecture

A single global deployment would be simpler, but it would also concentrate risk. A defective release, a stalled consumer, or one fleet producing abnormal volume could affect every operation.

The architecture is therefore divided into cells. On Azure, this design maps to the Deployment Stamps pattern: independent, repeatable workload copies that each serve a subset of customers or fleets.

Each cell owns its compute, Service Bus namespace, operational data, identity, and capacity limits. The control plane stores the `fleetId -> cellId` mapping but does not participate in route processing.

This separation creates three important properties:

1. A failure remains contained within the affected cell.
2. Capacity grows by adding cells with known limits.
3. Releases can progress gradually, starting with a canary cell.

The tradeoff is real: duplicated infrastructure, additional routing, and aggregated observability. Cell-based architecture is not a free optimization. It is a deliberate decision to reduce blast radius.

Source: [Microsoft Deployment Stamps pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/deployment-stamp).

## Why Service Bus

Service Bus provides queues, topics, subscriptions, sessions, duplicate detection, and DLQs. These features do not automatically make a consumer "exactly once." In PeekLock mode, a message may be delivered again if processing finishes before the receiver settles it.

The design therefore combines a business-derived `MessageId`, broker duplicate detection, and idempotent consumers.

Source: [Microsoft guidance for preventing message loss and duplicate processing](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-message-loss-and-duplicates).

## Infrastructure is part of the product

Infrastructure should not be a sequence of manual portal clicks. Namespaces, topics, subscriptions, queues, observability, and identities are defined in Terraform and reviewed through the same workflow as the Java code.

<!-- Next section: the first domain event and route risk detection test. -->
