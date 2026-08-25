# Fleet Routing Platform

Projeto de referência para explorar Spring Boot, Spring AI e serviços Azure em um cenário de transporte e logística orientado a eventos.

O objetivo é construir, em pequenas entregas, um copiloto operacional que interpreta telemetria, consulta serviços determinísticos de roteamento e propõe ações explicáveis para um operador humano.

## Estado atual

- [x] Bootstrap do projeto
- [x] Decisões de arquitetura e baseline Terraform do Service Bus
- [x] Domínio de frota, frete e telemetria
- [x] Detecção de risco de rota
- [ ] Orquestração assíncrona
- [ ] Integração com Azure Maps
- [ ] Integração com Spring AI e Azure OpenAI
- [ ] Observabilidade e resiliência

## Executar

### Toolchain suportada

- Java 25 LTS (`25.0.4-tem` no SDKMAN)
- Maven 3.9.16
- Spring Boot 4.1.1
- Spring Framework 7.0.9, gerenciado pelo Spring Boot
- Spring AI 2.0.1

Ative as versões fixadas pelo projeto:

```bash
sdk env
```

O Maven Enforcer interrompe o build quando ele é executado fora da família Java 25 ou com Maven anterior ao 3.9.16.

```bash
mvn spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

## Documentação

- [Arquitetura](docs/architecture.md)
- [ADRs](docs/adr)
- [Draft do artigo para LinkedIn](docs/linkedin-article-draft.md)
- [Infraestrutura Terraform](infra/terraform)
