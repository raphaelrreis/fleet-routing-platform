# Logistics Copilot

Projeto de referência para explorar Spring Boot, Spring AI e serviços Azure em um cenário de transporte e logística orientado a eventos.

O objetivo é construir, em pequenas entregas, um copiloto operacional que interpreta telemetria, consulta serviços determinísticos de roteamento e propõe ações explicáveis para um operador humano.

## Estado atual

- [x] Bootstrap do projeto
- [ ] Domínio de frota, frete e telemetria
- [ ] Detecção de risco de rota
- [ ] Orquestração assíncrona
- [ ] Integração com Azure Maps
- [ ] Integração com Spring AI e Azure OpenAI
- [ ] Observabilidade e resiliência

## Executar

```bash
mvn spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

