locals {
  migration_service_map = {
    aws = {
      "ECS Fargate or EKS" = "Azure Container Apps or AKS"
      "SQS and SNS"        = "Azure Service Bus"
      "MSK or Kinesis"     = "Azure Event Hubs"
      "RDS for PostgreSQL" = "Azure Database for PostgreSQL Flexible Server"
      "S3"                 = "Azure Blob Storage or Data Lake Storage"
      "Secrets Manager"    = "Azure Key Vault"
      "CloudWatch"         = "Azure Monitor and Application Insights"
    }
    gcp = {
      "Cloud Run or GKE"     = "Azure Container Apps or AKS"
      "Pub/Sub"              = "Azure Service Bus or Event Hubs"
      "Cloud SQL PostgreSQL" = "Azure Database for PostgreSQL Flexible Server"
      "Cloud Storage"        = "Azure Blob Storage or Data Lake Storage"
      "Secret Manager"       = "Azure Key Vault"
      "Cloud Monitoring"     = "Azure Monitor and Log Analytics"
      "Vertex AI"            = "Microsoft Foundry and Azure OpenAI"
    }
  }
}

output "source_cloud_discovery" {
  description = "Read-only source-cloud context used during migration planning."
  value = {
    aws = {
      provider_alias = "aws.source"
      primary_region = var.aws_source_region
    }
    gcp = {
      provider_alias = "google.source"
      primary_region = var.gcp_source_region
    }
  }
}

output "migration_service_map" {
  description = "Source-to-target service decisions for the Azure migration."
  value       = local.migration_service_map
}
