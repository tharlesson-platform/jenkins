# Imagem Base de Agente

Esta imagem base padroniza a inicializacao do inbound-agent com resolucao automatica de segredo.

## Base incluida

- `jenkins/inbound-agent` (variante JDK21)
- `curl`, `git`, `jq`, `bash`, `unzip`
- ponto de entrada `register-agent.sh` para registro no controller

## Comportamento em execucao

1. Aguarda disponibilidade do Jenkins.
2. Resolve `JENKINS_AGENT_SECRET` via API se nao estiver definido.
3. Inicia `/usr/local/bin/jenkins-agent`.
