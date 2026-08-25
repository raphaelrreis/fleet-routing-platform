# Terraform Azure Baseline

This directory provisions the initial cell-based infrastructure baseline:

- a shared resource group and Log Analytics workspace used only for aggregated observability;
- two independent cells by default: `brs-01` and `eus-01`;
- a dedicated resource group and Azure Service Bus namespace for each cell;
- a `logistics-events` topic, `route-planning` subscription, and DLQ in each cell;
- a `route-replanning-commands` queue in each cell;
- diagnostic settings and `cell-id` tags.

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
cp terraform.tfvars.example terraform.tfvars
az login
terraform init
terraform plan
```

Do not commit the real `terraform.tfvars` file. A remote state backend and deployment pipeline will be added before the first shared deployment.

## Decisions

- The `azurerm` provider is pinned for reproducible plans.
- Development uses the Standard tier because the Basic tier does not support topics.
- `local_auth_enabled = false`: workloads must use Microsoft Entra ID and Managed Identity.
- Broker duplicate detection does not replace consumer idempotency.
- At least two cells are required to expose single-instance assumptions during development.
- The Azure Verified Service Bus module is still on a zero-major release. This baseline uses explicit resources to keep the learning path and execution plan transparent.
