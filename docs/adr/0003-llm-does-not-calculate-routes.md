# ADR 0003 - The LLM Does Not Calculate Routes

- Status: Accepted
- Date: 2026-08-24

## Context

Generative models are effective at interpretation and explanation, but they do not provide mathematical or geographic guarantees for distance, road constraints, or optimal vehicle assignment.

## Decision

Spring AI will expose typed tools for querying fleet data, shipments, telemetry, and Azure Maps. The LLM may select and combine these tools, but it must not invent coordinates, costs, or routes.

The application validates the final structured recommendation, and the MVP requires human approval before execution.

## Consequences

- Geographic results remain auditable and reproducible.
- Tool calling has explicit limits, and destructive tools are never registered as defaults.
- The application, not the model, controls authentication, authorization, and side effects.
- Prompts and responses provide supporting context but are never the sole evidence behind a decision.
