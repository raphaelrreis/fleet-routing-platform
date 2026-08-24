# ADR 0003 — O LLM não calcula rotas

- Status: aceito
- Data: 2026-08-24

## Contexto

Modelos generativos são bons em interpretação e explicação, mas não fornecem garantia matemática ou geográfica para distâncias, restrições viárias e alocação ótima de veículos.

## Decisão

Spring AI disponibilizará ferramentas tipadas para consultar frota, frete, telemetria e Azure Maps. O LLM poderá escolher e combinar essas ferramentas, mas não poderá inventar coordenadas, custos ou rotas.

A recomendação final será um objeto estruturado validado pela aplicação e exigirá aprovação humana no MVP.

## Consequências

- resultados geográficos permanecem auditáveis e reproduzíveis;
- tool calling terá limites e ferramentas destrutivas não serão registradas como defaults;
- a aplicação, e não o modelo, controla autenticação, autorização e efeitos colaterais;
- prompts e respostas serão dados de apoio, nunca a única evidência da decisão.

