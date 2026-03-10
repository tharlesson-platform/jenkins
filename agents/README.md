# Catalogo de Agentes Jenkins

Todas as imagens sao agentes inbound e sao opcionais via perfis do compose.

## Agentes disponiveis

- `agent-java`: JDK 21, Maven, Gradle
- `agent-python`: Python 3, pip, venv, pytest, coverage, bandit, pip-audit
- `agent-node`: Node LTS, npm, corepack/yarn
- `agent-go`: Go, golangci-lint
- `agent-docker`: Docker CLI, buildx, compose plugin, trivy, git, curl, jq
- `agent-kubernetes`: kubectl, helm, yq, awscli opcional

## Registro em execucao

Cada imagem usa `agents/base/register-agent.sh` como ponto de entrada:

1. aguarda o Jenkins controller
2. le o segredo JNLP pela API do controller (se o segredo nao for informado)
3. inicia `/usr/local/bin/jenkins-agent`

## Variaveis de ambiente comuns

- `JENKINS_URL` ou `JENKINS_INTERNAL_URL`
- `JENKINS_ADMIN_USER`
- `JENKINS_ADMIN_PASSWORD`
- `JENKINS_AGENT_NAME`
- `JENKINS_AGENT_SECRET` (opcional)
- `JENKINS_WEB_SOCKET=true`
