# Monorepo da Plataforma Jenkins (Controller + Agents)

Inicializacao de plataforma Jenkins pronta para producao, com abordagem 100% como codigo:

1. Jenkins Controller (compatibilidade com termo legado: master)
2. Jenkins Agents opcionais (compatibilidade com termo legado: workers)
3. JCasC, scripts de inicializacao e seed jobs versionados em Git
4. Perfis opcionais para SonarQube e Nexus
5. Pipelines CI/CD e biblioteca compartilhada incluidos
6. Builds CI executados em containers base por linguagem (parametrizavel)

## Visao geral de arquitetura

Componentes principais:

1. `jenkins-controller`
2. `agent-java`, `agent-python`, `agent-node`, `agent-go`, `agent-docker`, `agent-kubernetes`
3. `sonarqube` + `sonarqube-db` (perfil `sonar`)
4. `nexus` (perfil `nexus`)
5. `nginx` (perfil `proxy`)

Detalhes: [docs/architecture.md](docs/architecture.md)

## Mapa do repositorio

1. `jenkins/`: imagem do controller, plugins, JCasC, init scripts, seed e biblioteca compartilhada
2. `agents/`: imagens dedicadas de agents por toolchain
3. `pipelines/`: Jenkinsfiles CI/CD e modelos
4. `examples/`: aplicacoes de exemplo para validacao
5. `scripts/`: inicializacao, testes de fumaca, backup/restauracao e auditoria de plugins
6. `docs/`: guias operacionais de arquitetura, seguranca, operacao e integracao de times

## Pre-requisitos

1. Docker 24+
2. Docker Compose v2+
3. Recursos recomendados:
   - 8 GB de RAM para stack completa
   - 4 vCPU para execucoes paralelas

## Inicio rapido (local)

1. Criar arquivo de ambiente:

```bash
cp .env.example .env
```

2. Subir controller:

```bash
docker compose up -d --build jenkins-controller
```

3. Subir servicos opcionais de qualidade/artefatos:

```bash
docker compose --profile sonar --profile nexus up -d --build
```

4. Subir todos os agents:

```bash
docker compose --profile agents up -d --build
```

5. Validar:

```bash
./scripts/smoke-tests.sh
```

## Perfis do Compose

1. Padrao: apenas controller
2. `sonar`: SonarQube + PostgreSQL
3. `nexus`: Nexus OSS
4. `proxy`: Nginx reverse proxy
5. `agents`: todos os agents
6. `agent-java|agent-python|agent-node|agent-go|agent-docker|agent-kubernetes`: agent individual

## Habilitar e desabilitar agents

Habilitar todos:

```bash
docker compose --profile agents up -d --build
```

Habilitar apenas Java e Docker:

```bash
docker compose --profile agent-java --profile agent-docker up -d --build
```

Parar todos os agents:

```bash
docker compose stop agent-java agent-python agent-node agent-go agent-docker agent-kubernetes
```

## Endpoints de acesso

1. Jenkins: `http://localhost:${JENKINS_HTTP_PORT:-8080}`
2. SonarQube: `http://localhost:${SONARQUBE_PORT:-9000}`
3. Nexus: `http://localhost:${NEXUS_PORT:-8081}`

## Configuracao de Active Directory

A autenticacao AD esta configurada via JCasC. Preencha no `.env`:

1. `AD_DOMAIN`
2. `AD_SERVERS`
3. `AD_BIND_DN`
4. `AD_BIND_PASSWORD`
5. Variaveis de grupos (`AD_GROUP_*`)

Guia completo: [docs/ad-auth.md](docs/ad-auth.md)

## Builds em container base

As pipelines CI usam `agent-docker` para orquestracao e executam compilacao/testes em imagens base parametrizaveis:

1. Java: `MAVEN_BUILD_IMAGE` e `GRADLE_BUILD_IMAGE`
2. Python: `PYTHON_BUILD_IMAGE`
3. Node: `NODE_BUILD_IMAGE`
4. Go: `GO_BUILD_IMAGE` e `GO_LINT_IMAGE`

## Ativos de pipelines entregues

1. Jenkinsfiles de CI:
   - `pipelines/ci/Jenkinsfile.java.ci`
   - `pipelines/ci/Jenkinsfile.python.ci`
   - `pipelines/ci/Jenkinsfile.node.ci`
   - `pipelines/ci/Jenkinsfile.go.ci`
   - `pipelines/ci/Jenkinsfile.container.ci`
2. Jenkinsfiles de CD:
   - `pipelines/cd/Jenkinsfile.docker.cd`
   - `pipelines/cd/Jenkinsfile.k8s.cd`
   - `pipelines/cd/Jenkinsfile.ecs.cd`
   - `pipelines/cd/Jenkinsfile.vm.cd`
3. Biblioteca compartilhada:
   - `jenkins/shared-library`

## Como testar as pipelines de exemplo

1. Execute `seed-local-files`.
2. Execute `Admin/Seed-From-Repo`.
3. Confirme jobs em `CI/*` e `CD/Apps/*`.
4. Comece com:
   - `CI/Java/java-maven-app-ci` usando `examples/java-maven-app`
   - `CI/Python/python-app-ci` usando `examples/python-app`
   - `CI/Node/node-app-ci` usando `examples/node-app`
   - `CI/Go/go-app-ci` usando `examples/go-app`
5. Ajuste os parametros `*_BUILD_IMAGE` para trocar versao/base da linguagem sem rebuild de agent.
6. Para CD em ECS, defina `INSTALL_AWSCLI=true` e reconstrua `agent-kubernetes`.

## Documentacao operacional e de seguranca

1. [docs/security.md](docs/security.md)
2. [docs/operations.md](docs/operations.md)
3. [docs/plugins.md](docs/plugins.md)
4. [docs/pipelines.md](docs/pipelines.md)
5. [docs/backup-restore.md](docs/backup-restore.md)
6. [docs/onboarding.md](docs/onboarding.md)
7. [docs/preflight-production.md](docs/preflight-production.md)

## Limitacoes conhecidas

1. Repositorios hosted/proxy/group do Nexus exigem inicializacao administrativa inicial.
2. Credenciais externas e ambientes de destino dependem da organizacao.
3. Pipeline de ECS depende de `awscli` no perfil `agent-kubernetes`.
4. TLS e integracao com gerenciador corporativo de segredos estao documentados, mas nao forcados por padrao.

## Preflight de producao

Execute o checklist automatizado de preflight:

```bash
./scripts/preflight-production.sh
```

Checklist completo e modelo de decisao GO/NO-GO:

- [docs/preflight-production.md](docs/preflight-production.md)

## Roteiro de evolucao

1. Automacao de seed em escala para multiplos repositorios/organizacoes.
2. Controles avancados de cadeia de suprimentos (assinatura, atestacao, gates de politica).
3. Perfil opcional de implantacao Jenkins nativo em Kubernetes.
4. Plano de observabilidade completa (metricas, logs, rastreamento).
