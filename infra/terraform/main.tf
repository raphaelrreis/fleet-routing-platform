resource "random_string" "suffix" {
  length  = 6
  upper   = false
  special = false
}

resource "azurerm_resource_group" "shared" {
  name     = "rg-${local.prefix}-shared"
  location = var.control_plane_location
  tags     = local.tags
}

resource "azurerm_log_analytics_workspace" "shared" {
  name                = "log-${local.prefix}-shared-${random_string.suffix.result}"
  location            = azurerm_resource_group.shared.location
  resource_group_name = azurerm_resource_group.shared.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
  tags                = local.tags
}

module "cell" {
  source = "./modules/cell"

  for_each = var.cells

  cell_id                    = each.key
  environment                = var.environment
  location                   = each.value.location
  service_bus_sku            = each.value.service_bus_sku
  workload                   = local.workload
  name_suffix                = random_string.suffix.result
  log_analytics_workspace_id = azurerm_log_analytics_workspace.shared.id
  tags                       = local.tags
}
