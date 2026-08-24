# Spring Boot + IA no Azure: um copiloto logístico para replanejar rotas com telemetria

> Draft em evolução. Cada seção só será considerada pronta quando o comportamento correspondente existir no repositório e estiver coberto por teste.

## Introdução

Um caminhão refrigerado está a caminho de uma entrega urgente. A temperatura da carga começa a subir, o combustível está abaixo do esperado e o trânsito indica que o prazo não será cumprido.

Qual caminhão deveria assumir o frete? Qual rota respeita as dimensões do veículo e as restrições da carga? E como explicar essa recomendação ao operador sem entregar uma decisão crítica a um modelo generativo?

Esse é o problema que escolhi para explorar Spring Boot, Spring AI e Azure de uma forma menos previsível que o tradicional chatbot.

A proposta não é pedir para um LLM “inventar a melhor rota”. O modelo atuará como copiloto: interpretará o incidente, consultará ferramentas tipadas e explicará uma alternativa produzida por componentes determinísticos.

## A primeira decisão: separar telemetria de workflow

Nem toda mensagem é igual.

Localização, velocidade e temperatura podem produzir milhares de sinais contínuos. Já eventos como `RouteRiskDetected` e `RouteProposed` representam mudanças importantes no processo de negócio e precisam de entrega confiável, retentativa e dead-letter queue.

Por isso, o projeto usa Azure Service Bus como backbone dos workflows. A ingestão massiva de telemetria será evoluída depois com IoT Hub/Event Hubs.

## A arquitetura inicial

<!-- Inserir diagrama depois que os primeiros adaptadores estiverem implementados. -->

O fluxo inicial será:

1. uma API Spring Boot recebe telemetria simulada;
2. regras determinísticas identificam risco para a entrega;
3. um evento `RouteRiskDetected` é publicado no Service Bus;
4. o worker consulta frota, restrições do frete e motor de rotas;
5. Spring AI usa essas informações para produzir uma recomendação estruturada;
6. um operador humano aprova ou rejeita a mudança.

## Por que uma arquitetura celular

Uma única instância global do sistema seria mais simples, mas também concentraria o risco. Uma implantação defeituosa, um consumidor travado ou uma frota produzindo volume anormal poderia afetar todas as operações.

Por isso, a arquitetura será dividida em células. No Azure esse desenho corresponde ao padrão Deployment Stamps: cópias independentes e repetíveis do workload, cada uma atendendo um subconjunto de clientes ou frotas.

Cada célula possui compute, namespace do Service Bus, dados operacionais, identidade e limites próprios. O control plane conhece o mapeamento `fleetId -> cellId`, mas não participa do processamento de uma rota.

Essa separação produz três propriedades importantes:

1. uma falha fica contida na célula afetada;
2. a capacidade cresce com a criação de novas células;
3. atualizações podem avançar gradualmente, começando por uma célula canário.

O custo é real: infraestrutura duplicada, roteamento adicional e observabilidade agregada. Arquitetura celular não é uma otimização gratuita; é uma escolha deliberada para reduzir o raio de impacto.

Fonte: [Microsoft — Deployment Stamps pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/deployment-stamp).

## Por que Service Bus

O Service Bus oferece filas, tópicos, assinaturas, sessões, detecção de duplicidade e DLQ. Entretanto, isso não transforma automaticamente o consumidor em “exactly once”. No modo `PeekLock`, uma mensagem pode ser entregue novamente se o processamento terminar antes da confirmação.

Por esse motivo, o desenho combina `MessageId` de negócio, detecção de duplicidade no broker e consumidores idempotentes.

Fonte: [Microsoft — evitar perda e duplicação de mensagens](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-message-loss-and-duplicates).

## Infraestrutura também é parte do artigo

Não quero que a infraestrutura seja uma lista de cliques no portal. Namespace, tópico, assinatura, fila, observabilidade e identidades serão definidos com Terraform e revisados no mesmo fluxo do código Java.

<!-- Próxima seção: primeiro evento de domínio e teste de detecção de risco. -->
