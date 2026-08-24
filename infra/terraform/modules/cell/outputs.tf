output "resource_group_name" {
  value = azurerm_resource_group.cell.name
}

output "service_bus_namespace" {
  value = azurerm_servicebus_namespace.cell.name
}

output "service_bus_fully_qualified_namespace" {
  value = "${azurerm_servicebus_namespace.cell.name}.servicebus.windows.net"
}

output "logistics_events_topic" {
  value = azurerm_servicebus_topic.logistics_events.name
}

output "route_replanning_queue" {
  value = azurerm_servicebus_queue.route_replanning_commands.name
}

