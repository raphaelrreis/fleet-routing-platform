# ADR 0005 - Multicloud Migration with Azure as the Target Platform

- Status: Accepted
- Date: 2026-08-24

## Context

The client currently operates workloads in AWS and GCP. A realistic Azure modernization cannot assume an empty starting point: source systems, data replication, operational coexistence, and rollback paths remain relevant until cutover is complete.

Permanent active-active multicloud would add latency, egress cost, inconsistent platform semantics, and a substantially larger operational surface. Those costs are not justified for this workload.

## Decision

Terraform configures Azure, AWS, and Google Cloud providers for the migration lifecycle.

- AWS and GCP are source environments.
- Source-cloud access is read-only and used for discovery, inventory, and migration verification.
- Azure is the target runtime and the default platform for new capabilities.
- The cell-based Fleet Routing Platform is deployed only on Azure.
- Migration proceeds through discovery, target build, replication, canary validation, cutover, stabilization, and source retirement.
- No cloud account, project, tenant, or subscription identifiers are committed.

## Boundaries

- Source providers must not create or mutate AWS or GCP resources.
- No synchronous cross-cloud call is introduced into the target application's steady-state critical path.
- Workloads that temporarily remain in a source cloud use explicit transitional connectivity and contracts.
- Every cutover has measurable acceptance criteria and a time-bounded rollback plan.

## Consequences

- The repository represents a credible brownfield migration rather than a greenfield Azure deployment.
- Provider and credential governance becomes more complex during transition.
- Cross-cloud data transfer and egress costs must be measured.
- Azure remains the long-term operational target, preventing indefinite multicloud sprawl.

## Sources

- [Microsoft Azure Migration Hub](https://learn.microsoft.com/en-us/azure/migration/)
- [Azure Cloud Adoption Framework migration execution](https://learn.microsoft.com/en-us/azure/cloud-adoption-framework/migrate/execute-migration)
- [Cross-region and multicloud connectivity](https://learn.microsoft.com/en-us/azure/networking/design-guide/cross-region)
- [Azure for AWS professionals](https://learn.microsoft.com/en-us/azure/architecture/aws-professional/services)
- [Migrate from GCP to Azure](https://learn.microsoft.com/en-us/azure/migration/migrate-from-google-cloud)
