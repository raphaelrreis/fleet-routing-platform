# AWS and GCP Migration to Azure

## Migration objective

Move the client's fleet routing workloads from AWS and GCP into independent Azure deployment cells while preserving service continuity, data integrity, observability, and rollback capability.

AWS and GCP remain source environments only during the migration. Azure becomes the target runtime and control plane.

## Service disposition

| Capability | AWS source | GCP source | Azure target |
|---|---|---|---|
| Container workload | ECS Fargate or EKS | Cloud Run or GKE | Azure Container Apps or AKS |
| Business messaging | SQS and SNS | Pub/Sub | Azure Service Bus |
| Event streaming | MSK or Kinesis | Pub/Sub | Azure Event Hubs |
| PostgreSQL | Amazon RDS | Cloud SQL | Azure Database for PostgreSQL Flexible Server |
| Object storage | Amazon S3 | Cloud Storage | Azure Blob Storage or Data Lake Storage |
| Secrets | Secrets Manager | Secret Manager | Azure Key Vault |
| Operations | CloudWatch | Cloud Monitoring | Azure Monitor, Log Analytics, and Application Insights |
| Generative AI | Bedrock | Vertex AI | Microsoft Foundry and Azure OpenAI |

The mapping is architectural, not a claim of feature parity. Each workload requires a detailed assessment of semantics, quotas, availability, security controls, and operational behavior.

## Migration phases

1. **Discover:** collect read-only inventory, dependencies, traffic, data volume, SLOs, and cost baselines from AWS and GCP.
2. **Assess:** classify each component as rehost, replatform, refactor, retain, or retire.
3. **Build the Azure target:** deploy landing-zone controls and at least two Fleet Routing Platform cells through Terraform.
4. **Replicate:** establish data synchronization and validate schema, counts, checksums, and replication lag.
5. **Canary:** route a bounded fleet segment to one Azure cell and compare business and operational metrics.
6. **Cut over:** freeze source writes when required, reach zero replication lag, update routing, and monitor acceptance criteria.
7. **Stabilize and retire:** maintain a time-bounded rollback window, then remove source resources and cross-cloud dependencies.

## Acceptance criteria

- No lost or duplicate business event outside documented at-least-once semantics.
- Shipment and vehicle state reconcile between source and target.
- Route assessment latency and error rate meet the agreed SLO.
- Azure cell capacity remains below the defined saturation threshold.
- Observability, security alerts, backup, restore, and DLQ replay are verified before production cutover.
- Rollback steps are tested before traffic migration.

## Credential model

- Azure authentication uses Azure CLI locally and workload identity in automation.
- AWS discovery uses a read-only profile supplied outside the repository.
- GCP discovery uses Application Default Credentials with read-only IAM roles.
- Terraform state and variable files never contain long-lived cloud credentials.
