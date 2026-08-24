# Terraform — Azure baseline

Este diretório provisiona o primeiro recorte de infraestrutura celular:

- resource group e Log Analytics compartilhados apenas para observabilidade agregada;
- duas células independentes por padrão (`brs-01` e `eus-01`);
- resource group e Azure Service Bus Namespace próprios por célula;
- tópico `logistics-events`, assinatura `route-planning` e DLQ por célula;
- fila `route-replanning-commands` por célula;
- diagnostic settings e tags com `cell-id`.

O módulo `modules/cell` é a unidade de repetição. Novas células são adicionadas ao mapa `cells`, mantendo a mesma topologia e os mesmos controles.

Toolchain validada: Terraform 1.15.8 ou patch posterior da linha 1.15.

## Validar localmente

```bash
terraform fmt -check -recursive
terraform init -backend=false
terraform validate
```

## Planejar uma implantação

```bash
cp terraform.tfvars.example terraform.tfvars
az login
terraform init
terraform plan
```

O `terraform.tfvars` real não deve ser versionado. O pipeline e o backend remoto de estado serão adicionados antes da primeira implantação compartilhada.

## Decisões

- Provider `azurerm` fixado para tornar o exemplo reproduzível.
- Standard no ambiente de desenvolvimento, pois tópicos não existem no Basic.
- `local_auth_enabled = false`: aplicações devem usar Entra ID/Managed Identity.
- Detecção de duplicidade não substitui idempotência no consumidor.
- Duas células são obrigatórias para detectar pressupostos de instância única ainda no desenvolvimento.
- O módulo Azure Verified para Service Bus ainda está em major version zero; este primeiro recorte usa recursos explícitos para deixar o aprendizado e o plano mais transparentes.
