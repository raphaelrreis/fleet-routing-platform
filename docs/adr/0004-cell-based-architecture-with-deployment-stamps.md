# ADR 0004 — Arquitetura celular com Azure Deployment Stamps

- Status: aceito
- Data: 2026-08-24

## Contexto

Uma única pilha global de aplicação, mensageria e dados aumenta o raio de impacto de falhas, implantações defeituosas e consumo excessivo por uma frota. O sistema também precisa crescer por unidades previsíveis de capacidade e permitir isolamento regional.

O Azure Architecture Center chama cada cópia independente de uma unidade de implantação de *stamp*, *scale unit* ou *cell*.

## Decisão

Adotaremos uma arquitetura celular baseada no padrão Azure Deployment Stamps.

Cada célula terá, no mínimo:

- compute do Logistics Copilot e do Route Planning Worker;
- namespace próprio do Azure Service Bus;
- tópicos, assinaturas, filas e DLQs próprios;
- armazenamento operacional particionado pela célula;
- identidade gerenciada, RBAC, métricas e limites de capacidade próprios.

Uma frota pertence a exatamente uma célula por vez. O control plane mantém o mapeamento `fleetId -> cellId`, capacidade e estado de cada célula. Mensagens carregam `cellId`, `fleetId` e um `MessageId` de negócio.

O MVP começa com duas células declaradas pelo mesmo módulo Terraform. Essa duplicação evita pressupostos ocultos de instância única antes de existirem usuários reais.

## Limites

- nenhuma chamada síncrona entre células no caminho crítico;
- nenhuma fila ou base operacional global compartilhada entre células;
- falha de uma célula não pode interromper a ingestão ou o planejamento das outras;
- analytics pode agregar eventos de todas as células fora do caminho transacional;
- movimentar uma frota entre células é um workflow explícito e auditável.

## Consequências

- menor raio de impacto e rollout progressivo por célula;
- escala horizontal por adição de células com capacidade conhecida;
- custo e complexidade operacional maiores;
- necessidade de control plane, roteamento e observabilidade agregada;
- Terraform precisa produzir células idênticas e impedir configuration drift.

## Fontes

- [Azure Architecture Center — Deployment Stamps pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/deployment-stamp)
- [Azure Service Bus em soluções multitenant](https://learn.microsoft.com/en-us/azure/architecture/guide/multitenant/service/service-bus)
- [AWS Well-Architected — benefícios de cell-based architecture](https://docs.aws.amazon.com/wellarchitected/latest/reducing-scope-of-impact-with-cell-based-architecture/why-to-use-a-cell-based-architecture.html)

