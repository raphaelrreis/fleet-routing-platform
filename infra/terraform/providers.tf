provider "azurerm" {
  features {}
}

provider "aws" {
  alias  = "source"
  region = var.aws_source_region
}

provider "google" {
  alias  = "source"
  region = var.gcp_source_region
}
