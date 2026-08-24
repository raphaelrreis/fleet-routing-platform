locals {
  workload = "logistics-copilot"
  prefix   = "lgc-${var.environment}"

  default_tags = {
    application = local.workload
    environment = var.environment
    managed-by  = "terraform"
    repository  = "logistics-copilot"
  }

  tags = merge(local.default_tags, var.tags)
}

