variable "subscription_id" {
  description = "Azure subscription that receives the development resources."
  type        = string
}

variable "control_plane_location" {
  description = "Azure region for shared control-plane observability resources."
  type        = string
  default     = "brazilsouth"
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
    eus-01 = {
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
