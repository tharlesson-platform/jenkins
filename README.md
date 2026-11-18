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

## CI de smoke

O repositorio agora inclui `.github/workflows/docker-smoke.yml` para:

1. validar scripts shell
2. renderizar `docker compose config`
3. subir o `jenkins-controller`
4. executar `scripts/smoke-tests.sh`

## Roteiro de evolucao

1. Automacao de seed em escala para multiplos repositorios/organizacoes.
2. Controles avancados de cadeia de suprimentos (assinatura, atestacao, gates de politica).
3. Perfil opcional de implantacao Jenkins nativo em Kubernetes.
4. Plano de observabilidade completa (metricas, logs, rastreamento).

## Licenca

Este projeto esta licenciado sob a Apache License 2.0. Consulte o arquivo `LICENSE` para mais detalhes.

## Atribuicao

Este projeto foi desenvolvido e publicado por **Tharlesson**.
Caso voce utilize este material como base em ambientes internos, estudos, adaptacoes ou redistribuicoes,
preserve os creditos de autoria e os avisos de licenca aplicaveis.

## Creditos e Uso

Este repositorio foi criado com foco em automacao, padronizacao operacional e melhoria da rotina de profissionais
de SRE, DevOps, Cloud e Plataforma.

Voce pode:
- estudar
- reutilizar
- adaptar
- evoluir este projeto dentro do seu contexto

Ao reutilizar ou derivar este material:
- mantenha os avisos de licenca
- preserve os creditos de autoria quando aplicavel
- documente alteracoes relevantes feitas sobre a base original

## Autor

**Tharlesson**  
GitHub: https://github.com/tharlesson

## Smoke de CI em VM ARM64 isolada

`Docker Smoke / smoke` e `guardrails` usam a label exclusiva
`jenkins-run-${{ github.run_id }}`, checkout da action por commit completo e
head exato do PR ou SHA do push, sem persistir credenciais. Os gatilhos PR/push
existentes são preservados; permissões são `contents: read`. Smoke tem timeout
de 30 minutos, guardrails de 20. Admissão por repo/workflow/job/evento/SHA pertence
ao controlador separado; esta fonte não instala perfil nem autoriza execução.

O smoke continua real: render config, build/up do controller, espera até
600 segundos por `/login`, smoke-tests, logs em falha e down -v. Executa somente
no Docker próprio de VM Linux ARM64 recém-criada, nunca no Docker/socket do Mac.
Antes de qualquer Compose mutante exige marker, fonte exata, ausência de mounts
compartilhados/SSH agent/gateway local de artifacts/credenciais ou perfis AWS,
endpoint `unix:///var/run/docker.sock`, contexto default e daemon Linux ARM64.
Qualquer container preexistente, inclusive parado, ou falha no inventário nega
execução. Contexto remoto/TLS/config arbitrário também é negado.
O único DOCKER_CONFIG aceito é `/opt/ci-docker-config`, com diretório e
`config.json` regulares, sem symlink, e SHA256 exato
`9dc4382b255bef99b5eb341a7c0a8d139ecf9c3f294711ebc7ebb765baa5c7d6`.
É o contrato local de VM isolada revisado: apenas proxies/default httpProxy, httpsProxy e
noProxy, HTTP/HTTPS em `http://192.168.5.100:3128`, sem auth/context/credenciais.
O proxy é preservado: Docker build RUN precisa dele para downloads. Qualquer
drift do arquivo exige nova revisão; a guarda não esvazia nem recria o config.
A guarda Linux ARM64/marker, ausência de host mounts e SHA exato continua
obrigatória antes desta exceção, que não permite outro diretório/endpoint.

A `.env` de CI é cópia da `.env.example` pública. Nenhum secret GitHub/cloud é
fornecido: senhas são fixtures públicas, AD permanece `example.local` e nenhum
AD é provisionado. `credentials.example.yaml` não integra a lista CASC ativa;
placeholders AWS/SSH do exemplo não são exportados para o controller. O login
verificado pelo smoke não comprova autenticação AD nem jornada administrativa.

Somente CI sobrescreve `JENKINS_JAVA_OPTS` para
`-Xms256m -Xmx768m -Djenkins.install.runSetupWizard=false`. A fixture original
pede heap de 2048 MiB, inadequado à VM de 2 GiB. Um override temporário acrescenta
`mem_limit: 1280m`, `cpus: 1.5`, nome do controller por projeto e portas
8080/50000 publicadas apenas em `127.0.0.1` do guest. Heap limitado não garante
build/plugins dentro do orçamento: aceite exige smoke real na VM.

O projeto `jenkins-ci-RUN_ID-RUN_ATTEMPT` é exclusivo; Compose recebe explicitamente
base + override existente + override temporário por COMPOSE_FILE. Ports usa
`!override`, exigindo Compose >= 2.24.4 para não manter publishes amplos por merge.
Logs e down -v só são habilitados após aprovação da guarda do guest Docker e
preparo do override; falha de guarda nunca aciona cleanup contra daemon incerto.
O socket montado pelo Compose é apenas o socket do Docker do guest. Os demais
binds são arquivos do checkout dentro da VM, sem mounts do host/Mac.

Scripts, init Groovy, CASC, Dockerfiles e Compose principais permanecem intactos.
Prova nativa desta preparação é apenas config puro com endpoint Unix inexistente,
HOME/Docker config próprios e AWS scrub, além de syntax/contratos com IO mockado.
Nenhum Docker build/run/up, serviço, AD, VM ou smoke real foi executado no host.
Revisão independente Main e smoke Linux ARM64 real permanecem pendentes.
Sem Root/runtime/API write, commit/push, produção, billing ou alterações nos
candidatos anteriores congelados. Rollback desta fase: não publicar/admitir a fonte.

O smoke test aceita a revisão de pacote Ubuntu na versão do Docker Compose
(por exemplo, `2.40.3+ds1-0ubuntu1~24.04.1`), mantendo o mínimo 2.24.4.

O controller converte apenas os mirrors oficiais Debian já configurados para
HTTPS antes do APT, com CA e assinaturas preservadas. O proxy isolado precisa
permitir os destinos oficiais de pacotes, Trivy e plugins Jenkins por HTTPS;
a alteração do proxy é validada e ativada após drenagem dos jobs em andamento.
A instalação de plugins no build usa o archive oficial HTTPS do Jenkins;
checksums e validações do instalador permanecem ativos.

O smoke isolado passa o proxy HTTP/HTTPS ao instalador Java por propriedades
JVM, pois o Java não utiliza automaticamente `HTTP_PROXY`/`HTTPS_PROXY`. O
argumento `JENKINS_PLUGIN_JAVA_OPTS` é opcional e restrito ao build; o override
CI também configura o Java do controller. Builds e runtime fora do CI mantêm
seus defaults. Nenhuma verificação TLS, assinatura APT ou checksum é desativada.

### Imagens dos exemplos e supply chain

As bases Docker são fixadas pelo digest do índice multiarch do registry oficial.
Atualizações de base exigem nova resolução, build e revisão da PR; os antigos
build-args de tag Jenkins não substituem mais a base revisada. Exemplos Java,
Node e Python executam como UID/GID 10001, sem privilégios. Diretórios da
aplicação permanecem somente leitura para esse usuário; temporários usam /tmp.
Nenhuma imagem é publicada ou promovida automaticamente por esta correção.
Reversão: reverter a PR após revisão, sem remover volumes ou dados.

### Runner Docker local isolado

O controlador provisiona proxy efêmero dentro da própria VM, no endpoint
192.168.5.100:3128, sem depender de outro host Root. O config é estático,
somente leitura e validado pelo SHA acima; noProxy aceita exclusivamente
localhost,127.0.0.1,::1. O proxy só aceita CONNECT HTTPS na porta 443 para
domínios de distribuição configurados (incluindo CDNs de múltiplos tenants)
e nega destinos privados/link-local. A ACL não comprova autoria/integridade
de conteúdo; checksums, assinaturas APT e o instalador Jenkins mantêm essa
validação separada. O job usa Docker rootless, obrigatório na guarda do
workflow. Root no container mapeia para ci-job/subuids, não root do guest;
não há grupo docker, sudo nem capacidade de alterar nft/Squid no namespace
pai. A política de egress é root-owned no guest e sobrevive ao privilégio
dos containers rootless. Nunca usa Docker/mounts/agent/cloud do Mac.
A VM deve ser descartada somente após conclusão e confirmação do job.

O cache/estado Buildx usa `/var/lib/ci-job/.docker-buildx`, separado do
Docker config estático somente leitura; não é necessário liberar escrita
em `/opt/ci-docker-config` para construir imagens.

### Regressão de build dos exemplos

O Docker Smoke também constrói e executa os quatro exemplos CLI (Java, Go,
Node e Python) no daemon rootless, sem publicar portas/imagens. Java executa
seus testes Maven antes do empacotamento; Node mantém o teste original,
JUnit e LCOV usando node:test nativo, com npm audit completo exigindo zero
achados. O lockfile é versionado, sem Jest/jest-junit/picomatch transitivos.
Go compila para TARGETOS/TARGETARCH do BuildKit e o smoke confere o campo
ELF, impedindo binário amd64 dentro de imagem ARM64 mesmo quando houver
emulação disponível. A execução readonly usa apenas /tmp efêmero.

O exemplo Java declara explicitamente Main-Class no manifest do JAR via
maven-jar-plugin fixado, para que `java -jar /app/app.jar` execute o mesmo
programa validado pelos testes Maven.

O Docker config protegido fica sob `/opt`, cujo diretório pai também pertence
a root; não fica sob o diretório gravável do runner. Assim ci-job não pode
renomear o diretório protegido para substituí-lo por outro config.
