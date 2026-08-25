locals {
  prefix = "frp-${var.environment}-${var.cell_id}"
  tags = merge(var.tags, {
    cell-id      = var.cell_id
    architecture = "cell-based"
    scale-unit   = "deployment-stamp"
  })
}

