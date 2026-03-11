#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

PASS_COUNT=0
FAIL_COUNT=0

pass() {
  PASS_COUNT=$((PASS_COUNT + 1))
  echo "[PASS] $*"
}

fail() {
  FAIL_COUNT=$((FAIL_COUNT + 1))
  echo "[FAIL] $*"
}

require_file() {
  local f="$1"
  if [ -f "$f" ]; then
    pass "Arquivo existe: $f"
  else
    fail "Arquivo ausente: $f"
  fi
}

check_cmd() {
  local c="$1"
  if command -v "$c" >/dev/null 2>&1; then
    pass "Comando disponivel: $c"
    return 0
  fi
  fail "Comando nao encontrado: $c"
  return 1
}

check_no_default_secret() {
  local key="$1"
  local bad_value="$2"
  local value
  value="$(grep -E "^${key}=" .env | sed -E "s/^${key}=//" || true)"
  if [ -z "$value" ]; then
    fail "Chave ausente no .env: $key"
    return
  fi
  if [ "$value" = "$bad_value" ]; then
    fail "Segredo padrao detectado no .env: $key"
  else
    pass "Chave de segredo customizada: $key"
  fi
}

echo "== Preflight de Producao da Plataforma Jenkins =="

require_file ".env"
require_file "docker-compose.yml"
require_file "jenkins/plugins.txt"
require_file "docs/preflight-production.md"

if [ -f ".env" ]; then
  check_no_default_secret "JENKINS_ADMIN_PASSWORD" "change_me_now"
  check_no_default_secret "AD_BIND_PASSWORD" "change_me_bind_password"
  check_no_default_secret "NEXUS_CI_PASSWORD" "change_me"
fi

if check_cmd docker && check_cmd curl; then
  if docker compose version >/dev/null 2>&1; then
    pass "docker compose disponivel"
  else
    fail "docker compose indisponivel"
  fi

  if docker compose config >/dev/null 2>&1; then
    pass "docker compose config validado"
  else
    fail "falha no docker compose config"
  fi

  if docker compose ps >/dev/null 2>&1; then
    pass "docker compose consulta servicos"
  else
    fail "falha no docker compose ps"
  fi

  JENKINS_URL_VALUE="$(grep -E '^JENKINS_URL=' .env | cut -d= -f2- || true)"
  if [ -z "$JENKINS_URL_VALUE" ]; then
    JENKINS_URL_VALUE="http://localhost:8080"
  fi

  if curl -fsS "${JENKINS_URL_VALUE%/}/login" >/dev/null 2>&1; then
    pass "Endpoint Jenkins acessivel: ${JENKINS_URL_VALUE%/}/login"
  else
    fail "Endpoint Jenkins inacessivel: ${JENKINS_URL_VALUE%/}/login"
  fi
fi

if [ -f "./scripts/smoke-tests.sh" ]; then
  pass "Script de testes de fumaca presente"
else
  fail "Script de testes de fumaca ausente: ./scripts/smoke-tests.sh"
fi

if [ -f "./scripts/plugin-audit.sh" ]; then
  pass "Script de auditoria de plugins presente"
else
  fail "Script de auditoria de plugins ausente: ./scripts/plugin-audit.sh"
fi

echo
echo "Resumo do preflight: PASS=${PASS_COUNT} FAIL=${FAIL_COUNT}"

if [ "$FAIL_COUNT" -gt 0 ]; then
  echo "Resultado do preflight: NO-GO"
  exit 1
fi

echo "Resultado do preflight: GO (checks de baseline)"
