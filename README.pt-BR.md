# Fleet Routing Platform

[English](README.md)

MVP funcional para transporte e logística, construído com Spring Boot, Spring AI e serviços Azure em uma arquitetura orientada a eventos.

A plataforma processa telemetria de veículos, aplica regras determinísticas de risco e produz recomendações explicáveis para incidentes operacionais.

## Principal funcionalidade de IA

O MVP concentra a IA na **recomendação assistida para incidentes**:

1. Regras determinísticas detectam risco de rota a partir da telemetria normalizada.
2. Uma transação de comando grava a avaliação e um evento de outbox de forma atômica.
3. Um worker assíncrono processa `RouteRiskDetected`.
4. O Spring AI envia somente os fatos verificados da avaliação ao Azure OpenAI.
5. O modelo devolve um `RouteRecommendation` tipado, com recomendação, justificativa e ações necessárias.
6. O endpoint de consulta CQRS expõe a avaliação e o status da recomendação.

A IA não calcula rotas, previsão de chegada, distâncias ou restrições de segurança. Essas decisões continuam determinísticas e auditáveis. Consulte [AI-assisted incident recommendation](docs/ai-incident-recommendation.md).

## Estado do projeto

- [x] Estrutura inicial do projeto
- [x] Decisões de arquitetura e baseline Terraform para Service Bus
- [x] Modelo de domínio de frota, carga e telemetria
- [x] Detecção de risco de rota
- [x] Contexto de migração de AWS e GCP com Azure como destino
- [x] Fluxos de comando e consulta em CQRS
- [x] Outbox transacional e processamento assíncrono
- [x] Adaptador Spring AI com Azure OpenAI
- [x] Publicação Kafka, Docker Compose e Helm chart para AKS
- [ ] Integração com Azure Maps
- [ ] Observabilidade gerenciada no Azure e testes de resiliência ponta a ponta

## Arquitetura de execução

- **PostgreSQL:** estado de comandos, projeção de consultas e outbox transacional.
- **Azure Service Bus:** comandos de negócio, coordenação de workflows, sessões e DLQs.
- **API Kafka:** streaming de telemetria e eventos de domínio. O ambiente local usa Redpanda; no Azure, Event Hubs com endpoint compatível com Kafka.
- **Docker Compose:** PostgreSQL e Redpanda para desenvolvimento local.
- **Kubernetes:** implantação via Helm no AKS, repetida para cada célula Azure.

## Execução local

Ferramentas suportadas:

- Java 25 LTS (`25.0.4-tem` via SDKMAN)
- Maven 3.9.16
- Spring Boot 4.1.1
- Spring Framework 7.0.9, gerenciado pelo Spring Boot
- Spring AI 2.0.1

```bash
sdk env
docker compose up -d postgres redpanda
SPRING_PROFILES_ACTIVE=kafka mvn spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

Os comandos completos para criar e consultar uma avaliação estão no [README em inglês](README.md#cqrs-api).

## Documentação

- [Arquitetura](docs/architecture.md)
- [Recomendação de incidente assistida por IA](docs/ai-incident-recommendation.md)
- [Migração de AWS e GCP para Azure](docs/cloud-migration.md)
- [ADRs](docs/adr)
- [Infraestrutura Terraform](infra/terraform)
