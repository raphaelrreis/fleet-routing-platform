resource "random_string" "suffix" {
  length  = 6
  upper   = false
  special = false
}

resource "azurerm_resource_group" "main" {
  name     = "rg-${local.prefix}"
  location = var.location
  tags     = local.tags
}

resource "azurerm_log_analytics_workspace" "main" {
  name                = "log-${local.prefix}-${random_string.suffix.result}"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
  tags                = local.tags
}

resource "azurerm_servicebus_namespace" "main" {
  name                = "sb-${local.prefix}-${random_string.suffix.result}"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  sku                 = var.service_bus_sku
  local_auth_enabled  = false
  minimum_tls_version = "1.2"
  tags                = local.tags

  identity {
    type = "SystemAssigned"
  }
}

resource "azurerm_servicebus_topic" "logistics_events" {
  name                                    = "logistics-events"
  namespace_id                            = azurerm_servicebus_namespace.main.id
  default_message_ttl                     = "P1D"
  requires_duplicate_detection            = true
  duplicate_detection_history_time_window = "PT10M"
  support_ordering                        = true
  partitioning_enabled                    = var.service_bus_sku == "Standard"
}

resource "azurerm_servicebus_subscription" "route_planning" {
  name                                      = "route-planning"
  topic_id                                  = azurerm_servicebus_topic.logistics_events.id
  max_delivery_count                        = 5
  lock_duration                             = "PT1M"
  default_message_ttl                       = "P1D"
  dead_lettering_on_message_expiration      = true
  dead_lettering_on_filter_evaluation_error = true
}

resource "azurerm_servicebus_queue" "route_replanning_commands" {
  name                                    = "route-replanning-commands"
  namespace_id                            = azurerm_servicebus_namespace.main.id
  default_message_ttl                     = "PT1H"
  lock_duration                           = "PT1M"
  max_delivery_count                      = 5
  dead_lettering_on_message_expiration    = true
  requires_duplicate_detection            = true
  duplicate_detection_history_time_window = "PT10M"
  partitioning_enabled                    = var.service_bus_sku == "Standard"
}

resource "azurerm_monitor_diagnostic_setting" "service_bus" {
  name                       = "service-bus-diagnostics"
  target_resource_id         = azurerm_servicebus_namespace.main.id
  log_analytics_workspace_id = azurerm_log_analytics_workspace.main.id

  enabled_log {
    category_group = "allLogs"
  }

  enabled_metric {
    category = "AllMetrics"
  }
}

