#!/usr/bin/env bash
set -euo pipefail

URL="${1:-}"
TIMEOUT_SECONDS="${2:-180}"

if [ -z "$URL" ]; then
  echo "Uso: $0 <url> [timeout_segundos]"
  exit 1
fi

START_TIME="$(date +%s)"

until curl -fsS "$URL" >/dev/null 2>&1; do
  NOW="$(date +%s)"
  ELAPSED=$((NOW - START_TIME))
  if [ "$ELAPSED" -ge "$TIMEOUT_SECONDS" ]; then
    echo "Timeout aguardando servico: $URL"
    exit 1
  fi
  echo "Aguardando $URL ... (${ELAPSED}s/${TIMEOUT_SECONDS}s)"
  sleep 5
done

echo "Servico pronto: $URL"

