#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker nao encontrado no PATH."
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "Docker Compose v2 nao encontrado."
  exit 1
fi

if [ ! -f ".env" ]; then
  cp .env.example .env
  echo ".env criado a partir de .env.example (atualize os segredos antes de usar em producao)."
fi

mkdir -p backups

echo "Subindo Jenkins Controller..."
docker compose up -d --build jenkins-controller

echo "Aguardando Jenkins ficar saudavel..."
"$ROOT_DIR/scripts/wait-for-services.sh" "http://localhost:${JENKINS_HTTP_PORT:-8080}/login" 180

echo "Inicializacao concluida com sucesso."

