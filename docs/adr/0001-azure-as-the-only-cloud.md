# ADR 0001 — Azure como única cloud

- Status: aceito
- Data: 2026-08-24

## Contexto

O projeto pretende demonstrar Spring Boot, AI Engineering e arquitetura distribuída em um cenário próximo de vagas corporativas que pedem experiência prática com Microsoft Azure.

## Decisão

Todos os recursos gerenciados de produção serão Azure. O provisionamento será feito exclusivamente por Terraform versionado no mesmo repositório.

O desenvolvimento local pode usar adaptadores em memória e simuladores, mas eles devem implementar as mesmas portas usadas pelos adaptadores Azure.

## Consequências

- reduzimos variações de configuração e identidade;
- conseguimos demonstrar Managed Identity, RBAC e observabilidade de ponta a ponta;
- aceitamos acoplamento operacional com Azure em troca de um exemplo mais coeso;
- cada novo recurso Azure deve entrar primeiro no Terraform e na documentação.

