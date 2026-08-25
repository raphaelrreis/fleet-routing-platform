# Spring Boot, IA e Azure: da telemetria de frota a uma recomendação acionável

[English](linkedin-article-draft.md)

> Rascunho. Cada comportamento descrito aqui está ligado a código executável e testes automatizados no repositório.

Uma carga refrigerada já está em trânsito quando a temperatura ultrapassa o limite contratado. O combustível está baixo e a telemetria mais recente aponta um atraso de 35 minutos.

Não quero que um LLM calcule a rota. Distância, previsão de chegada, restrições do veículo e regras de segurança pertencem a componentes determinísticos, testáveis e auditáveis.

O problema interessante começa depois que a plataforma confirma o risco: como transformar esses fatos em uma recomendação operacional clara, sem permitir que o modelo invente informações?

Essa é a principal funcionalidade do MVP que estou construindo com Spring Boot, Spring AI e Azure: **recomendação assistida por IA para incidentes de rota**.

## A funcionalidade de ponta a ponta

O fluxo implementado funciona assim:

1. Regras determinísticas avaliam a telemetria normalizada e detectam o risco da rota.
2. A transação de comando grava a avaliação e um evento de outbox de forma atômica no PostgreSQL.
3. Um worker assíncrono processa `RouteRiskDetected`.
4. O Spring AI envia ao Azure OpenAI somente os fatos verificados da avaliação.
5. O modelo precisa devolver um `RouteRecommendation` tipado: `recommendation`, `rationale` e `requiredActions`.
6. O endpoint de consulta CQRS expõe a avaliação, os motivos que dispararam o risco e o status da recomendação.

Se a chamada de IA falhar, a avaliação não desaparece. O worker aplica retry com backoff exponencial e, depois de esgotar as tentativas, move o evento para um estado de dead letter. A consulta continua mostrando o que aconteceu, em vez de esconder a falha atrás de um timeout.

A fronteira é objetiva: a IA interpreta o incidente e organiza os próximos passos. Ela não calcula rotas, distância, ETA ou restrições de segurança. Azure Maps e os serviços determinísticos do domínio serão responsáveis por esses cálculos.

## Por que CQRS e outbox transacional

Telemetria chega rápido. Uma resposta de IA depende de um serviço remoto, com latência e disponibilidade diferentes. Manter a requisição HTTP aberta durante todo esse processamento acoplaria a entrada de dados ao modelo.

O command side tem uma responsabilidade curta: normalizar o payload recebido, executar as regras de risco e confirmar a avaliação junto com o evento de outbox. As duas gravações acontecem ou nenhuma acontece.

O processamento assíncrono gera a recomendação e atualiza o modelo de leitura. O cliente consulta outro endpoint para acompanhar o status: `PENDING`, `COMPLETED` ou `FAILED`.

É uma aplicação pragmática de CQRS. Não são necessários dois bancos nem um framework adicional. A separação está no comportamento: um caminho altera o estado; o outro entrega a visão operacional.

## A telemetria não entra diretamente no domínio

O payload do sistema de telemetria informa velocidade em metros por segundo, combustível como uma razão entre zero e um e atraso em segundos. O domínio trabalha com quilômetros por hora, percentual e minutos.

Uma anti-corruption layer traduz formatos, unidades e nomes antes que as regras sejam executadas. O vocabulário do fornecedor termina nessa fronteira, e o domínio não precisa mudar sempre que a integração muda.

## Kafka e Service Bus têm papéis diferentes

A plataforma usa a API Kafka para streaming de telemetria e eventos de domínio. No desenvolvimento local, Redpanda oferece esse contrato. No Azure, Event Hubs fornece um endpoint compatível com Kafka.

Azure Service Bus fica com comandos de negócio e coordenação de workflows que precisam de filas, sessões, retries explícitos e DLQ. Colocar todos os tipos de mensagem no mesmo produto apagaria diferenças operacionais importantes.

A própria documentação da Microsoft descreve a compatibilidade Kafka do Event Hubs e o modelo de settlement do Service Bus, incluindo a possibilidade de uma mensagem ser entregue novamente. Idempotência ainda é responsabilidade do consumidor; uma configuração do broker não produz semântica exatamente uma vez de ponta a ponta.

Fontes: [Event Hubs para Apache Kafka](https://learn.microsoft.com/pt-br/azure/event-hubs/azure-event-hubs-kafka-overview) e [transferências, locks e settlement no Service Bus](https://learn.microsoft.com/pt-br/azure/service-bus-messaging/message-transfers-locks-settlement).

## Azure como destino de uma arquitetura celular

O runtime de destino é dividido em células com base no padrão Deployment Stamps do Azure. Cada célula contém seus workloads Spring Boot, recursos de mensageria, dados operacionais, identidade e limites de capacidade. Cada frota é atribuída a uma célula.

Assim, uma release com problema ou um pico anormal de telemetria afeta uma parcela conhecida da operação. O rollout também pode avançar gradualmente: implanta-se em uma célula, mede-se o comportamento e só então a versão segue adiante.

Existe um custo: infraestrutura repetida, roteamento mais cuidadoso e observabilidade agregada. A escolha reduz blast radius, mas não elimina complexidade.

Fonte: [padrão Deployment Stamps no Azure](https://learn.microsoft.com/pt-br/azure/architecture/patterns/deployment-stamp).

## O multicloud é uma etapa da migração

O cenário parte de workloads existentes em AWS e GCP e leva essa capacidade para Azure. O Terraform configura os três providers, mas cada um tem um papel claro:

- AWS e GCP representam os ambientes de origem usados para inventário, coexistência, replicação e validação do cutover.
- Azure recebe todos os novos componentes e será o runtime definitivo.

Workloads de ECS, EKS, Cloud Run e GKE seguem para AKS ou Azure Container Apps. Workflows baseados em SQS, SNS e Pub/Sub migram para Service Bus. MSK, Kinesis e fluxos de streaming do Pub/Sub convergem para Event Hubs. PostgreSQL em RDS ou Cloud SQL passa para Azure Database for PostgreSQL Flexible Server.

Não se trata de manter active-active entre três clouds indefinidamente. Depois da estabilização no Azure e do aceite dos critérios de cutover, o caminho de origem é desativado.

Fonte: [Azure Migration and Modernization Center](https://azure.microsoft.com/pt-br/products/azure-migrate/).

## O que já pode ser executado

O repositório usa Java 25 LTS, Spring Boot 4.1, Spring AI 2.0, PostgreSQL, Kafka, Docker, Kubernetes e Terraform.

Docker Compose inicia PostgreSQL e Redpanda para desenvolvimento local. Um adaptador determinístico permite testar todo o fluxo de incidente sem credenciais de cloud. Ao ativar o profile `azure-ai`, a porta de recomendação passa a usar Azure OpenAI. Um Helm chart prepara o workload para AKS, enquanto o Terraform define o baseline repetível das células no Azure.

O MVP continua pequeno, mas já permite discutir problemas que aparecem em produção: atomicidade, processamento assíncrono, retry, resposta tipada de IA, CQRS, anti-corruption layer e migração entre clouds.

Repositório: [github.com/raphaelrreis/fleet-routing-platform](https://github.com/raphaelrreis/fleet-routing-platform)

O próximo incremento conecta Azure Maps, adiciona testes de resiliência ponta a ponta e provisiona os serviços gerenciados da primeira célula no Azure.
