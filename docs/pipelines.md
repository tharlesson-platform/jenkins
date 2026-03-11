# Pipelines

## Estrategia

1. CI e CD sao separados por responsabilidade.
2. Todas as pipelines CI executam em `agent-docker` e compilam/testam dentro de container base da linguagem.
3. Imagens base sao parametrizaveis por pipeline (`*_BUILD_IMAGE`) para reduzir acoplamento de versao nos agentes.
4. Etapas da biblioteca compartilhada sao usadas para controles reutilizaveis.

## Mapeamento de labels de agent

1. CI (Java/Python/Node/Go/Container): `agent-docker`
2. CD Docker/VM: `agent-docker`
3. CD Kubernetes e ECS: `agent-kubernetes`

## Jenkinsfiles CI entregues

1. `pipelines/ci/Jenkinsfile.java.ci`
2. `pipelines/ci/Jenkinsfile.python.ci`
3. `pipelines/ci/Jenkinsfile.node.ci`
4. `pipelines/ci/Jenkinsfile.go.ci`
5. `pipelines/ci/Jenkinsfile.container.ci`

## Jenkinsfiles CD entregues

1. `pipelines/cd/Jenkinsfile.docker.cd`
2. `pipelines/cd/Jenkinsfile.k8s.cd`
3. `pipelines/cd/Jenkinsfile.ecs.cd`
4. `pipelines/cd/Jenkinsfile.vm.cd`

## Parametros de imagem base por stack

1. Java:
   - `MAVEN_BUILD_IMAGE`
   - `GRADLE_BUILD_IMAGE`
2. Python:
   - `PYTHON_BUILD_IMAGE`
3. Node:
   - `NODE_BUILD_IMAGE`
4. Go:
   - `GO_BUILD_IMAGE`
   - `GO_LINT_IMAGE`

## Cobertura de estagios CI por stack

### CI Java

1. Coleta do codigo
2. Compilacao/testes em container base (Maven ou Gradle)
3. Publicacao de JUnit e cobertura
4. Analise Sonar (opcional)
5. Dependency-check (opcional)
6. Publicacao de artefato no Nexus (opcional)
7. Construcao de container + analise Trivy (opcional)
8. Publicacao de container (opcional)

### CI Python

1. Coleta do codigo
2. Build/testes em container base
3. Analise de seguranca (bandit + pip-audit) em container base (opcional)
4. Analise Sonar (opcional)
5. Empacotamento/publicacao em container base (opcional)
6. Construcao de container + analise Trivy (opcional)
7. Publicacao de container (opcional)

### CI Node

1. Coleta do codigo
2. Build/testes em container base
3. Analise Sonar (opcional)
4. Empacotamento/publicacao em container base (opcional)
5. Construcao de container + analise Trivy (opcional)
6. Publicacao de container (opcional)

### CI Go

1. Coleta do codigo
2. fmt/vet/testes/cobertura em container base
3. golangci-lint em container base
4. Compilacao de binario em container base
5. Analise Sonar (opcional)
6. Publicacao de artefato no repo raw do Nexus (opcional)
7. Construcao de container + analise Trivy (opcional)
8. Publicacao de container (opcional)

### CI de Container

1. Coleta do codigo
2. Validacao de Dockerfile
3. Construcao de imagem com buildx
4. Estrategia multi-tag: branch + SHA curto + tag de release
5. Analise Trivy
6. Geracao opcional de SBOM
7. Publicacao opcional

## Comportamentos de CD

### CD Docker/VM

1. Etapa opcional de aprovacao
2. Implantacao por versao de imagem
3. Atualizacao remota de compose
4. Parametro opcional de imagem para reversao

### CD Kubernetes

1. Etapa opcional de aprovacao
2. Atualizacao de imagem na implantacao
3. Verificacao de status do rollout
4. Revisao opcional para reversao

### CD ECS

1. Etapa opcional de aprovacao
2. Clonagem de task definition com troca de imagem
3. Atualizacao de servico com espera de estabilizacao
4. Reversao opcional para ARN de task definition informado

### CD de Artefato em VM

1. Etapa opcional de aprovacao
2. Extracao de release versionada no servidor remoto
3. Troca de symlink `current`
4. Reversao opcional para diretorio de release anterior

## Jobs semeados

### Pipelines tradicionais

1. `CI/Java/java-maven-app-ci`
2. `CI/Python/python-app-ci`
3. `CI/Node/node-app-ci`
4. `CI/Go/go-app-ci`
5. `CI/Containers/container-image-ci`
6. `CD/Apps/docker-cd`
7. `CD/Apps/k8s-cd`
8. `CD/Apps/ecs-cd`
9. `CD/Apps/vm-cd`

### Pipelines multibranch

1. `CI/Java/java-maven-app-mb`
2. `CI/Python/python-app-mb`
3. `CI/Node/node-app-mb`
4. `CI/Go/go-app-mb`
5. `CI/Containers/container-image-mb`

## Uso da biblioteca compartilhada

Caminho da biblioteca: `jenkins/shared-library`

Principais etapas reutilizaveis:

1. `checkoutSource`
2. `runInBaseContainer`
3. `runSonarScan`
4. `runTrivyScan`
5. `publishJUnit`
6. `publishArtifact`
7. `notifyBuild`
8. `validateRef`

## Notas de ambiente

1. Pipeline de ECS exige `awscli` no `agent-kubernetes`.
2. Habilite com `INSTALL_AWSCLI=true` e reconstrua a imagem do agent.
3. Implantacoes de producao devem usar restricoes por branch/tag de release.
