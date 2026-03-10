# Operacoes

## Pre-requisitos

1. Docker Engine 24+
2. Docker Compose v2+
3. Minimo de 8 GB RAM para controller + sonar + nexus
4. Arquivo `.env` criado a partir de `.env.example`

## Fluxo padrao de subida

1. Preparar ambiente:

```bash
cp .env.example .env
```

2. Subir controller:

```bash
docker compose up -d --build jenkins-controller
```

3. Subir servicos opcionais:

```bash
docker compose --profile sonar --profile nexus up -d --build
```

4. Subir agents:

```bash
docker compose --profile agents up -d --build
```

## Matriz de perfis

1. Padrao: Jenkins controller.
2. `sonar`: SonarQube + PostgreSQL.
3. `nexus`: Nexus OSS.
4. `proxy`: Nginx reverse proxy.
5. `agents`: todos os agents.
6. `agent-java|python|node|go|docker|kubernetes`: perfil de agent individual.

## Verificacoes de saude

1. Status de servicos:

```bash
docker compose ps
```

2. Endpoints:
   - Jenkins: `/login`
   - SonarQube: `/api/system/health`
   - Nexus: `/service/rest/v1/status`

3. Suite de testes de fumaca:

```bash
./scripts/smoke-tests.sh
```

## Logs e diagnostico

1. Acompanhar logs:

```bash
docker compose logs -f jenkins-controller
docker compose logs -f sonarqube
docker compose logs -f nexus
```

2. Problemas comuns:
   - Jenkins nao fica saudavel:
     - validar fase de instalacao de plugins nos logs
     - confirmar sintaxe JCasC e compatibilidade de plugins
   - Agent offline:
     - validar se o nome do node bate com JCasC
     - validar credenciais de admin usadas para buscar secret
     - validar conectividade do agent com URL interna do controller
   - Sonar/Nexus indisponivel:
     - verificar permissoes de volume
     - verificar recursos disponiveis no servidor

## Guia de inicializacao de pipelines

1. Executar `seed-local-files`.
2. Executar `Admin/Seed-From-Repo`.
3. Confirmar jobs em:
   - `CI/*`
   - `CD/Apps/*`
4. Para valores padrao do seu repositorio, ajustar:
   - `SEED_DEFAULT_REPO_URL`
   - `SEED_DEFAULT_BRANCH`

## Gestao de mudanca e upgrades

### Upgrade de Jenkins core

1. Atualizar `JENKINS_VERSION` no `.env`.
2. Revisar compatibilidade de plugins.
3. Reconstruir a imagem do controller:

```bash
docker compose build --no-cache jenkins-controller
```

4. Reiniciar stack e rodar testes de fumaca.

### Upgrade de plugins

1. Atualizar `jenkins/plugins.txt`.
2. Reconstruir a imagem do controller.
3. Rodar `./scripts/plugin-audit.sh`.
4. Validar seed e pipelines de exemplo.

## Como adicionar novo agent

1. Adicionar Dockerfile em `agents/<stack>/`.
2. Adicionar servico e perfil no `docker-compose.yml`.
3. Adicionar node em `jenkins/casc/nodes.yaml`.
4. Atualizar labels em `docs/pipelines.md`.
5. Reconstruir e validar conectividade.

## Como adicionar novo Jenkinsfile

1. Adicionar Jenkinsfile em `pipelines/ci` ou `pipelines/cd`.
2. Reusar biblioteca compartilhada antes de criar logica customizada.
3. Adicionar entrada no seed DSL em `jenkins/seed-jobs/jobdsl/base.groovy`.
4. Reexecutar `seed-local-files`.

## Como adicionar novo time/projeto

1. Criar pasta e nome de jobs conforme `pipelines/templates/naming-conventions.md`.
2. Registrar credenciais e repositorios de artefatos.
3. Comecar com app correspondente em `examples/`.
4. Habilitar CD somente apos estabilidade de CI.

## Operacao de backup e restauracao

1. Backup:

```bash
./scripts/backup-jenkins.sh
```

2. Restauracao:

```bash
./scripts/restore-jenkins.sh ./backups/<arquivo-backup>.tar.gz
```

3. Guia completo: `docs/backup-restore.md`

## Checklist operacional pos-subida

1. Controller saudavel e acessivel.
2. Login AD funcionando para usuarios de teste.
3. RBAC mapeado conforme esperado.
4. Agents necessarios online.
5. Seed jobs executados com sucesso.
6. Pelo menos um CI e um CD de exemplo concluido.
7. Backup e restauracao testados em nao-producao.
8. Auditoria de plugins executada e registrada.

## Execucao de preflight de producao

1. Executar:

```bash
./scripts/preflight-production.sh
```

2. Revisar checklist e modelo de decisao:
   - `docs/preflight-production.md`
3. Se script ou etapa obrigatoria falhar, declarar NO-GO.

