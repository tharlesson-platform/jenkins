# Guia de Inicializacao do Nexus

## Objetivo

Configurar repositorios hosted/proxy/group para Java, Node, Python, Go e Docker.

## Sequencia recomendada

1. Criar blob store dedicado (opcional).
2. Maven:
   - `maven-central` (proxy)
   - `maven-releases` (hosted)
   - `maven-snapshots` (hosted)
   - `maven-public` (group)
3. npm:
   - `npmjs` (proxy)
   - `npm-hosted` (hosted)
   - `npm-group` (group)
4. PyPI:
   - `pypi-proxy` (proxy)
   - `pypi-hosted` (hosted)
   - `pypi-group` (group)
5. Docker:
   - `docker-hub` (proxy)
   - `docker-hosted` (hosted, porta 8082)
   - `docker-group` (group)
6. Criar usuarios de CI com permissao minima.
7. Criar tokens/senhas para publicacao.

## Limitacao conhecida

A criacao inicial dos repositorios geralmente exige autenticacao administrativa no Nexus. Por isso, o processo completo de inicializacao esta documentado e pode ser automatizado depois via API/script com credenciais apropriadas.

