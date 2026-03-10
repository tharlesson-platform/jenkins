# Plugins

Fonte da verdade: [`jenkins/plugins.txt`](../jenkins/plugins.txt)

## Catalogo de plugins e finalidade

### Configuration as Code e inicializacao

1. `configuration-as-code`: carrega linha base do Jenkins por YAML.
2. `job-dsl`: gera pastas/jobs a partir de codigo.

### Mecanismo de pipeline e bibliotecas compartilhadas

1. `workflow-aggregator`: pacote principal de plugins de pipeline.
2. `workflow-multibranch`: execucao de pipeline multibranch.
3. `pipeline-stage-view`: visualizacao classica de estagios.
4. `pipeline-utility-steps`: etapas utilitarias comuns em Jenkinsfiles.
5. `pipeline-model-definition`: sintaxe declarativa de pipeline.
6. `pipeline-github-lib`: obtencao de biblioteca compartilhada via GitHub.
7. `pipeline-groovy-lib`: suporte a biblioteca compartilhada global.
8. `cloudbees-folder`: hierarquia e organizacao por pastas.

### SCM e integracao de repositorio

1. `git`: integracao SCM Git.
2. `git-client`: backend de operacoes Git de baixo nivel.
3. `github`: capacidades de integracao com GitHub.
4. `github-branch-source`: descoberta multibranch para GitHub.
5. `gitlab-plugin`: hooks e integracao com GitLab.
6. `branch-api`: abstracao branch/PR para jobs multibranch.
7. `scm-api`: API generica para plugins SCM.

### Identidade, credenciais e autorizacao

1. `credentials`: armazenamento base de credenciais.
2. `credentials-binding`: injeta credenciais em variaveis de ambiente.
3. `plain-credentials`: suporte a credenciais de texto/segredo.
4. `ssh-credentials`: suporte a chaves SSH.
5. `active-directory`: autenticacao AD.
6. `role-strategy`: modelo de autorizacao por papeis.
7. `matrix-auth`: compatibilidade de permissoes em matriz.
8. `mask-passwords`: mascara segredos no console.

### Confiabilidade de compilacao e higiene de diretorio de trabalho

1. `timestamper`: logs de console com timestamp.
2. `ws-cleanup`: limpeza do diretorio de trabalho antes/depois de compilacao.
3. `ansicolor`: cores ANSI no console.
4. `build-timeout`: aplica timeout de execucao.

### Testes, qualidade e relatorios

1. `junit`: publicacao de relatorios JUnit.
2. `warnings-ng`: agregacao de relatorios de analise estatica.
3. `jacoco`: publicacao de cobertura Java.
4. `htmlpublisher`: publicacao de relatorios HTML.
5. `sonar`: integracao com SonarQube.
6. `dependency-check-jenkins-plugin`: publicacao OWASP dependency-check.

### Fluxos de container, cluster e artefatos

1. `docker-workflow`: comandos Docker em pipelines.
2. `docker-plugin`: agents Jenkins dinamicos baseados em Docker.
3. `kubernetes`: integracao cloud/agent com Kubernetes.
4. `kubernetes-cli`: etapas auxiliares de kubectl.
5. `nexus-artifact-uploader`: etapa de upload para Nexus.
6. `gradle`: auxiliares de integracao Gradle.
7. `nodejs`: instalacoes de ferramentas Node.js.

### Notificacoes, controles e governanca

1. `email-ext`: notificacoes avancadas por email.
2. `slack`: notificacoes Slack.
3. `office-365-connector`: webhook Teams/Office365.
4. `lockable-resources`: serializa uso de implantacao/infra compartilhada.
5. `generic-webhook-trigger`: acionamento por webhook generico.
6. `git-parameter`: parametros para selecao branch/tag.
7. `ssh-agent`: agente de chave SSH para etapas de implantacao.

### Interface opcional

1. `blueocean`: camada moderna de interface (opcional em producao).

## Politica minima para producao

1. Manter conjunto de plugins o menor possivel.
2. Fixar versoes apos validar linha base.
3. Atualizar apenas em janela de manutencao.
4. Validar com pipelines de fumaca antes de promover para producao.
5. Manter reversao de imagem/tag do controller para regressao de plugin.

## Fluxo de auditoria

1. Revisar conjunto declarado:

```bash
grep -vE '^\s*#|^\s*$' jenkins/plugins.txt
```

2. Comparar com ambiente em execucao:

```bash
./scripts/plugin-audit.sh
```

3. Investigar:
   - plugins em execucao nao declarados
   - plugins obsoletos
   - avisos de compatibilidade plugin/core
