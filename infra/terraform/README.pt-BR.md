# Baseline Terraform no Azure

[English](README.md)

Este diretório provisiona a infraestrutura inicial baseada em células:

- um resource group compartilhado e um Log Analytics Workspace somente para observabilidade agregada;
- duas células independentes por padrão: `brs-01` e `eus2-01`;
- um resource group e um namespace do Azure Service Bus dedicados por célula;
- um tópico `logistics-events`, uma subscription `route-planning` e sua DLQ por célula;
- uma fila `route-replanning-commands` por célula;
- configurações de diagnóstico e tags `cell-id`.

O Terraform também configura aliases dos providers AWS e Google Cloud para descoberta somente leitura durante a migração. Eles representam os ambientes de origem; todos os recursos criados por este baseline pertencem ao ambiente Azure de destino.

O módulo `modules/cell` é a unidade repetível de implantação. Novas células são adicionadas ao mapa `cells`, preservando a mesma topologia e os mesmos controles em cada deployment stamp.

Toolchain validada: Terraform 1.15.8 ou patch posterior da linha 1.15.

## Validação local

```bash
terraform fmt -check -recursive
terraform init -backend=false
terraform validate
```

## Planejamento de uma implantação

```bash
az login
export ARM_SUBSCRIPTION_ID="$(az account show --query id -o tsv)"
terraform init
terraform plan -var-file=environments/dev.tfvars
```

As identidades de AWS e Google Cloud devem ter apenas permissões de leitura e ser fornecidas fora do repositório. IDs de assinatura e tenant são obtidos pela autenticação do Azure CLI e por variáveis de ambiente; nenhum segredo é versionado.

## Decisões

- O provider `azurerm` permanece fixado para produzir planos reproduzíveis.
- O ambiente de desenvolvimento usa a camada Standard porque a Basic não oferece suporte a tópicos.
- `local_auth_enabled = false`: workloads devem usar Microsoft Entra ID e Managed Identity.
- A detecção de duplicidade do broker não substitui a idempotência do consumidor.
- Pelo menos duas células são mantidas durante o desenvolvimento para revelar suposições de instância única.
- Os aliases de AWS e GCP delimitam as origens da migração; somente o Azure é provisionado como destino.
