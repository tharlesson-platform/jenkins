#!/usr/bin/env bash
set -euo pipefail

log() {
  echo "[agent-bootstrap] $*"
}

JENKINS_URL="${JENKINS_URL:-http://jenkins-controller:8080}"
JENKINS_AGENT_NAME="${JENKINS_AGENT_NAME:-$(hostname)}"
JENKINS_AGENT_WORKDIR="${JENKINS_AGENT_WORKDIR:-/home/jenkins/agent}"
AGENT_MAX_WAIT_SECONDS="${AGENT_MAX_WAIT_SECONDS:-300}"

wait_for_jenkins() {
  local start now elapsed
  start="$(date +%s)"
  while true; do
    if curl -fsS "${JENKINS_URL%/}/login" >/dev/null 2>&1; then
      log "Jenkins acessivel em ${JENKINS_URL}."
      return 0
    fi
    now="$(date +%s)"
    elapsed="$((now - start))"
    if [ "$elapsed" -ge "$AGENT_MAX_WAIT_SECONDS" ]; then
      log "Tempo esgotado aguardando Jenkins apos ${AGENT_MAX_WAIT_SECONDS}s."
      return 1
    fi
    log "Aguardando Jenkins... (${elapsed}s/${AGENT_MAX_WAIT_SECONDS}s)"
    sleep 5
  done
}

fetch_secret_if_needed() {
  if [ -n "${JENKINS_SECRET:-}" ] && [ -z "${JENKINS_AGENT_SECRET:-}" ]; then
    export JENKINS_AGENT_SECRET="${JENKINS_SECRET}"
  fi

  if [ -n "${JENKINS_AGENT_SECRET:-}" ]; then
    log "Usando JENKINS_AGENT_SECRET fornecido pelo ambiente."
    return 0
  fi

  if [ -z "${JENKINS_ADMIN_USER:-}" ] || [ -z "${JENKINS_ADMIN_PASSWORD:-}" ]; then
    log "JENKINS_AGENT_SECRET nao foi definido e credenciais de admin nao foram informadas."
    log "Defina JENKINS_AGENT_SECRET ou informe JENKINS_ADMIN_USER/JENKINS_ADMIN_PASSWORD."
    return 1
  fi

  local jnlp_url xml secret
  jnlp_url="${JENKINS_URL%/}/computer/${JENKINS_AGENT_NAME}/jenkins-agent.jnlp"
  log "Buscando secret JNLP para o node '${JENKINS_AGENT_NAME}'."
  xml="$(curl -fsS -u "${JENKINS_ADMIN_USER}:${JENKINS_ADMIN_PASSWORD}" "${jnlp_url}")"
  secret="$(printf '%s' "${xml}" | awk -F'[<>]' '/<argument>/{print $3; exit}')"

  if [ -z "${secret}" ]; then
    log "Nao foi possivel extrair o secret do agent em ${jnlp_url}."
    log "Garanta que o node '${JENKINS_AGENT_NAME}' existe no JCasC e que o Jenkins esta saudavel."
    return 1
  fi

  export JENKINS_AGENT_SECRET="${secret}"
  log "Secret do agent obtido via API do Jenkins."
}

main() {
  mkdir -p "${JENKINS_AGENT_WORKDIR}"
  wait_for_jenkins
  fetch_secret_if_needed

  export JENKINS_URL
  export JENKINS_AGENT_NAME
  export JENKINS_AGENT_WORKDIR
  export JENKINS_WEB_SOCKET="${JENKINS_WEB_SOCKET:-true}"
  export JENKINS_SECRET="${JENKINS_AGENT_SECRET}"

  log "Iniciando inbound agent '${JENKINS_AGENT_NAME}' com labels '${JENKINS_AGENT_LABELS:-}'."
  exec /usr/local/bin/jenkins-agent
}

main "$@"
