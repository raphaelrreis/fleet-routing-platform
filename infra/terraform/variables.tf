variable "subscription_id" {
  description = "Azure subscription that receives the development resources."
  type        = string
}

variable "location" {
  description = "Primary Azure region."
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

variable "service_bus_sku" {
  description = "Service Bus tier. Standard for development; Premium is recommended for isolated production capacity."
  type        = string
  default     = "Standard"

  validation {
    condition     = contains(["Standard", "Premium"], var.service_bus_sku)
    error_message = "service_bus_sku must be Standard or Premium."
  }
}

variable "tags" {
  description = "Additional resource tags."
  type        = map(string)
  default     = {}
}

