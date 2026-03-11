#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

JENKINS_URL="${JENKINS_URL:-http://localhost:${JENKINS_HTTP_PORT:-8080}}"
SONAR_URL="http://localhost:${SONARQUBE_PORT:-9000}"
NEXUS_URL="http://localhost:${NEXUS_PORT:-8081}"

echo "Executando testes de fumaca..."

echo "- Endpoint de login do Jenkins"
curl -fsS "$JENKINS_URL/login" >/dev/null

if docker compose ps --status running sonarqube | grep -q sonarqube; then
  echo "- Endpoint de saude do SonarQube"
  curl -fsS "$SONAR_URL/api/system/health" >/dev/null
fi

if docker compose ps --status running nexus | grep -q nexus; then
  echo "- Endpoint de status do Nexus"
  curl -fsS "$NEXUS_URL/service/rest/v1/status" >/dev/null
fi

echo "Testes de fumaca concluidos com sucesso."
