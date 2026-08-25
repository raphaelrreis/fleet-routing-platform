# ADR 0001 - Azure as the Sole Cloud Provider

- Status: Accepted
- Date: 2026-08-24

## Context

The project demonstrates Spring Boot, AI engineering, and distributed architecture in an enterprise scenario that requires practical Microsoft Azure experience.

## Decision

All managed production resources will run on Azure. Terraform versioned in this repository is the only supported provisioning mechanism.

Local development may use in-memory adapters and simulators, but they must implement the same application ports as the Azure adapters.

## Consequences

- Configuration and identity variations are reduced.
- The project demonstrates Managed Identity, RBAC, and end-to-end observability.
- The design accepts operational Azure coupling in exchange for a cohesive reference implementation.
- Every new Azure resource must be introduced through Terraform and documented before use.
