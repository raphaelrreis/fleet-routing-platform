# ADR 0002 - Service Bus for Business Workflows

- Status: Accepted
- Date: 2026-08-24

## Context

The system processes two distinct message categories:

1. Business commands and workflow events that must not be lost.
2. High-frequency telemetry where throughput and analytical retention are more important.

## Decision

Azure Service Bus will carry business commands and workflow events. Topics support multiple independent consumers, while queues carry commands targeted at one consumer type.

A future phase will evaluate IoT Hub and Event Hubs for high-volume telemetry ingestion. Raw GPS readings will not be sent through Service Bus.

## Consequences

- PeekLock processing provides *at-least-once* delivery.
- Stable `MessageId` values, broker duplicate detection, and idempotent consumers work together.
- The DLQ is treated as an operational queue with metrics and a controlled replay process.
- Event streaming and enterprise messaging remain explicitly separated.
