# ADR 0002 — Service Bus para workflows de negócio

- Status: aceito
- Data: 2026-08-24

## Contexto

O sistema processará dois tipos diferentes de mensagem:

1. comandos e eventos de negócio, que não podem ser perdidos;
2. telemetria de alta frequência, em que throughput e retenção para análise são mais importantes.

## Decisão

Azure Service Bus será usado para comandos e eventos de workflow. Usaremos tópicos quando houver múltiplos consumidores e filas para comandos destinados a um único tipo de consumidor.

IoT Hub/Event Hubs será avaliado em uma etapa futura para ingestão massiva de telemetria. Não enviaremos toda leitura bruta de GPS para o Service Bus.

## Consequências

- processamento em `PeekLock` e, portanto, *at least once*;
- `MessageId` estável, detecção de duplicidade e consumidor idempotente;
- DLQ tratada como fila operacional, com métricas e processo de reenvio;
- separação explícita entre event streaming e mensageria empresarial.

