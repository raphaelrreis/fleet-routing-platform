# AI-Assisted Incident Recommendation

## Feature boundary

AI begins after the platform has established the facts of an incident. Route risk, telemetry normalization, geographic constraints, and route calculations remain deterministic.

```text
Telemetry Command
      |
      v
Anti-Corruption Layer
      |
      v
Deterministic Risk Rules
      |
      +---- atomic transaction ----+
      |                            |
      v                            v
Assessment Projection         Outbox Event
                                   |
                                   v
                         Asynchronous Worker
                                   |
                         +---------+---------+
                         |                   |
                         v                   v
                   Spring AI           Kafka / Event Hubs
                         |
                         v
                  Azure OpenAI
                         |
                         v
              Typed RouteRecommendation
                         |
                         v
                    CQRS Query
```

## Model input

The prompt contains only the stored assessment:

- cell, shipment, and truck identifiers;
- assessment timestamp;
- deterministic severity;
- verified risk reasons.

The model is explicitly prohibited from inventing routes, distances, ETAs, coordinates, or fleet availability.

## Model output

Spring AI maps structured output into `RouteRecommendation`:

- `recommendation`: the proposed dispatch response;
- `rationale`: a concise explanation based on supplied facts;
- `requiredActions`: ordered steps for dispatch review.

Schema validation retries malformed model output before the outbox retry policy takes over. The query model reports `PENDING`, `COMPLETED`, or `FAILED` recommendation status.

## Profiles

- The `azure-ai` profile enables Spring AI with Azure OpenAI.
- The local profile uses the same interface and produces a deterministic operational recommendation without cloud credentials.
- The `kafka` profile publishes `RouteRiskDetected` through the Kafka API. Azure Event Hubs provides the production Kafka-compatible endpoint.

## Failure handling

The outbox worker uses bounded exponential backoff. Exhausted events move to `DEAD_LETTERED`, and the query model exposes a failed recommendation status. The command transaction remains committed and can be replayed safely because the assessment ID is deterministic.
