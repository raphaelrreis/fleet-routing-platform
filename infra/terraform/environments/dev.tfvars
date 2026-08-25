environment            = "dev"
control_plane_location = "brazilsouth"
aws_source_region      = "us-east-1"
gcp_source_region      = "us-central1"

cells = {
  brs-01 = {
    location        = "brazilsouth"
    service_bus_sku = "Standard"
  }
  eus2-01 = {
    location        = "eastus2"
    service_bus_sku = "Standard"
  }
}

tags = {
  owner               = "fleet-platform-team"
  business-unit       = "transportation"
  cost-center         = "fleet-operations"
  criticality         = "tier-2"
  data-classification = "internal"
}
