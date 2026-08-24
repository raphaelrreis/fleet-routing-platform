output "resource_group_name" {
  description = "Resource group created for the workload."
  value       = azurerm_resource_group.main.name
}

output "service_bus_namespace" {
  description = "Service Bus namespace name consumed by the Spring application."
  value       = azurerm_servicebus_namespace.main.name
}

output "service_bus_fully_qualified_namespace" {
  description = "Passwordless Service Bus endpoint."
  value       = "${azurerm_servicebus_namespace.main.name}.servicebus.windows.net"
}

output "logistics_events_topic" {
  value = azurerm_servicebus_topic.logistics_events.name
}

output "route_replanning_queue" {
  value = azurerm_servicebus_queue.route_replanning_commands.name
}

