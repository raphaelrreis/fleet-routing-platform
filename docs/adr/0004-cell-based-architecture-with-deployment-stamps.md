# ADR 0004 - Cell-Based Architecture with Azure Deployment Stamps

- Status: Accepted
- Date: 2026-08-24

## Context

A single global application, messaging, and data stack increases the blast radius of failures, defective deployments, and excessive workload from one fleet. The system must also scale through predictable capacity units and support regional isolation.

The Azure Architecture Center describes each independent deployment unit as a *stamp*, *scale unit*, or *cell*.

## Decision

The platform will use a cell-based architecture implemented through the Azure Deployment Stamps pattern.

At a minimum, each cell contains:

- Fleet Routing Platform and Route Planning Worker compute;
- a dedicated Azure Service Bus namespace;
- dedicated topics, subscriptions, queues, and DLQs;
- operational storage partitioned by cell;
- dedicated managed identity, RBAC, metrics, and capacity limits.

A fleet belongs to exactly one cell at a time. The control plane stores the `fleetId -> cellId` assignment together with each cell's capacity and status. Messages carry `cellId`, `fleetId`, and a business-derived `MessageId`.

The MVP starts with two cells produced by the same Terraform module. This duplication exposes hidden single-instance assumptions before real users depend on the platform.

## Boundaries

- No synchronous cross-cell calls exist in the critical path.
- Cells do not share a global operational queue or database.
- A cell failure must not interrupt ingestion or route planning in other cells.
- Analytics may aggregate events from all cells outside the transactional path.
- Moving a fleet between cells is an explicit, auditable workflow.

## Consequences

- Failures have a smaller blast radius, and releases can roll out progressively by cell.
- Horizontal scale comes from adding cells with known capacity.
- Infrastructure cost and operational complexity increase.
- The platform requires a control plane, request routing, and aggregated observability.
- Terraform must produce identical cells and prevent configuration drift.

## Sources

- [Azure Architecture Center Deployment Stamps pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/deployment-stamp)
- [Azure Service Bus in multitenant solutions](https://learn.microsoft.com/en-us/azure/architecture/guide/multitenant/service/service-bus)
- [AWS Well-Architected benefits of cell-based architecture](https://docs.aws.amazon.com/wellarchitected/latest/reducing-scope-of-impact-with-cell-based-architecture/why-to-use-a-cell-based-architecture.html)
