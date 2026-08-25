variable "control_plane_location" {
  description = "Azure region for shared control-plane observability resources."
  type        = string
  default     = "brazilsouth"
}

variable "aws_source_region" {
  description = "Primary AWS region inspected during migration discovery."
  type        = string
  default     = "us-east-1"
}

variable "gcp_source_region" {
  description = "Primary GCP region inspected during migration discovery."
  type        = string
  default     = "us-central1"
}

variable "environment" {
  description = "Deployment environment name."
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "stg", "prod"], var.environment)
    error_message = "environment must be dev, stg, or prod."
  }
}

variable "cells" {
  description = "Independent deployment stamps. Each map key is the stable cell identifier."
  type = map(object({
    location        = string
    service_bus_sku = string
  }))

  default = {
    brs-01 = {
      location        = "brazilsouth"
      service_bus_sku = "Standard"
    }
    eus2-01 = {
      location        = "eastus2"
      service_bus_sku = "Standard"
    }
  }

  validation {
    condition     = length(var.cells) >= 2
    error_message = "Cell-based deployments require at least two cells to prevent single-cell assumptions."
  }

  validation {
    condition = alltrue([
      for cell in values(var.cells) : contains(["Standard", "Premium"], cell.service_bus_sku)
    ])
    error_message = "Every cell service_bus_sku must be Standard or Premium."
  }
}

variable "tags" {
  description = "Additional resource tags."
  type        = map(string)
  default     = {}
}
