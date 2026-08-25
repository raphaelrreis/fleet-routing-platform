terraform {
  required_version = ">= 1.15.8, < 1.16.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "5.2.0"
    }
    aws = {
      source  = "hashicorp/aws"
      version = "6.57.1"
    }
    google = {
      source  = "hashicorp/google"
      version = "7.45.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.7"
    }
  }
}
