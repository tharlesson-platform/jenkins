# Integracao Nexus OSS

O Nexus e o repositorio central de artefatos para Maven, npm, PyPI, Go e imagens Docker.

## Acesso

- UI: `http://localhost:${NEXUS_PORT:-8081}`
- Docker hosted (exemplo): `localhost:${NEXUS_DOCKER_PORT:-8082}`

## Primeiro acesso

1. Obter senha admin inicial:
   `docker compose exec nexus cat /nexus-data/admin.password`
2. Fazer login com usuario `admin`.
3. Alterar a senha.
4. Seguir o guia de inicializacao em [`bootstrap.md`](./bootstrap.md).

