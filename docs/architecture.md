# Arquitetura do Logistics Copilot

## Objetivo

Construir um copiloto operacional para transporte rodoviário capaz de receber sinais de telemetria, identificar risco de uma entrega e propor um replanejamento explicável. A decisão geográfica será calculada por um motor determinístico; o LLM será usado para orquestrar ferramentas e explicar a recomendação.

## Princípios

1. **Azure-only:** todos os serviços gerenciados de produção pertencem ao ecossistema Azure.
2. **Infrastructure as Code:** nenhum recurso permanente será criado manualmente no portal; Terraform é a fonte de verdade.
3. **Mensageria confiável:** Azure Service Bus transporta comandos e eventos de workflow.
4. **Telemetria não é comando:** sinais de alta frequência serão encaminhados por IoT Hub/Event Hubs em uma etapa posterior.
5. **LLM não calcula rotas:** Azure Maps e um solver determinístico calculam custo, restrições e rota.
6. **Human in the loop:** uma recomendação de alteração operacional exige aprovação humana no MVP.
7. **Passwordless first:** workloads de produção usarão Managed Identity e RBAC.

## Contexto

```text
Caminhão / Simulador
        |
        v
Telemetry API (Spring Boot)
        |
        | RouteRiskDetected
        v
Azure Service Bus
        |
        v
Route Planning Worker (Spring Boot)
        |             |
        |             +--> Azure Maps / solver
        |
        +----------------> Spring AI / Azure OpenAI
                              |
                              v
                    Recomendação estruturada
                              |
                              v
                       Aprovação humana
```

## Eventos iniciais

| Evento | Emissor | Consumidor | Finalidade |
|---|---|---|---|
| `TelemetryReceived` | Telemetry API | Risk Detector | Registrar uma leitura normalizada |
| `RouteRiskDetected` | Risk Detector | Route Planning Worker | Solicitar análise de risco |
| `ReplanningRequested` | Route Planning Worker | Route Engine | Calcular alternativas determinísticas |
| `RouteProposed` | Logistics Copilot | Operations API | Apresentar recomendação ao operador |
| `RouteApproved` | Operations API | Dispatch Adapter | Autorizar mudança operacional |

## Service Bus

O baseline usa:

- tópico `logistics-events`, com detecção de duplicidade;
- assinatura `route-planning`, com `PeekLock`, tentativas limitadas e DLQ;
- fila `route-replanning-commands`, reservada para comandos direcionados;
- `MessageId` derivado do identificador do processo de negócio;
- consumidores idempotentes, porque `PeekLock` oferece entrega *at least once*.

## Fontes oficiais

- [Azure Well-Architected Framework — Service Bus](https://learn.microsoft.com/en-us/azure/well-architected/service-guides/azure-service-bus)
- [Como evitar perda e duplicação de mensagens](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-message-loss-and-duplicates)
- [Queues, topics e subscriptions](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-queues-topics-subscriptions)
- [Dead-letter queues](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-dead-letter-queues)
- [Azure Maps — rotas para caminhões](https://learn.microsoft.com/en-us/rest/api/maps/route/post-route-directions?view=rest-maps-2025-01-01)
- [Spring AI — tool calling](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [OpenAI Docs — Microsoft Azure OpenAI](https://developers.openai.com/api/reference/ruby#microsoft-azure-openai)
- [Terraform Provider AzureRM — Service Bus](https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/resources/servicebus_namespace)

