variable "cell_id" {
  description = "Stable identifier used for routing fleets and messages to this cell."
  type        = string
}

variable "environment" {
  type = string
}

variable "workload" {
  type = string
}

variable "location" {
  type = string
}

variable "service_bus_sku" {
  type = string
}

variable "name_suffix" {
  type = string
}

variable "log_analytics_workspace_id" {
  type = string
}

variable "tags" {
  type = map(string)
}

