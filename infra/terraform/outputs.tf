output "shared_resource_group_name" {
  description = "Resource group for shared control-plane observability."
  value       = azurerm_resource_group.shared.name
}

output "cells" {
  description = "Per-cell resource groups and passwordless Service Bus endpoints."
  value = {
    for cell_id, cell in module.cell : cell_id => {
      resource_group_name                   = cell.resource_group_name
      service_bus_namespace                 = cell.service_bus_namespace
      service_bus_fully_qualified_namespace = cell.service_bus_fully_qualified_namespace
      logistics_events_topic                = cell.logistics_events_topic
      route_replanning_queue                = cell.route_replanning_queue
    }
  }
}
