# Terraform Azure Baseline

[Português (Brasil)](README.pt-BR.md)

This directory provisions the initial cell-based infrastructure baseline:

- a shared resource group and Log Analytics workspace used only for aggregated observability;
- two independent cells by default: `brs-01` and `eus2-01`;
- a dedicated resource group and Azure Service Bus namespace for each cell;
- a `logistics-events` topic, `route-planning` subscription, and DLQ in each cell;
- a `route-replanning-commands` queue in each cell;
- diagnostic settings and `cell-id` tags.

Terraform also configures aliased AWS and Google Cloud providers for read-only migration discovery. These providers represent source environments; all resources created by this baseline are Azure target resources.

The `modules/cell` module is the repeatable deployment unit. Add cells to the `cells` map to preserve the same topology and controls across every deployment stamp.

Validated toolchain: Terraform 1.15.8 or a later patch release in the 1.15 line.

## Local validation

```bash
terraform fmt -check -recursive
terraform init -backend=false
terraform validate
```

## Planning a deployment

```bash
az login
export ARM_SUBSCRIPTION_ID="$(az account show --query id -o tsv)"

# Supply read-only source-cloud identities outside the repository when discovery is enabled.
# AWS_PROFILE and Google Application Default Credentials are resolved by their providers.

terraform init
terraform plan -var-file=environments/dev.tfvars
```

Subscription and tenant identifiers come from Azure CLI authentication and environment variables; they are never committed. The repository contains environment-specific, non-secret workload settings under `environments/`. A remote state backend and deployment pipeline will be added before the first shared deployment.

## Decisions

- The `azurerm` provider is pinned for reproducible plans.
- Development uses the Standard tier because the Basic tier does not support topics.
- `local_auth_enabled = false`: workloads must use Microsoft Entra ID and Managed Identity.
- Broker duplicate detection does not replace consumer idempotency.
- At least two cells are required to expose single-instance assumptions during development.
- The Azure Verified Service Bus module is still on a zero-major release. This baseline uses explicit resources to keep the topology auditable and provider behavior visible.
- AWS and GCP provider aliases are read-only migration boundaries; Azure is the only target that this baseline provisions.
