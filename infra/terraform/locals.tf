locals {
  workload = "fleet-routing-platform"
  prefix   = "frp-${var.environment}"

  default_tags = {
    application = local.workload
    environment = var.environment
    managed-by  = "terraform"
    repository  = "fleet-routing-platform"
  }

  tags = merge(local.default_tags, var.tags)
}

