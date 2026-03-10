#!/usr/bin/env bash
set -euo pipefail

if [ $# -lt 1 ]; then
  echo "Uso: $0 <arquivo-backup.tar.gz>"
  exit 1
fi

BACKUP_FILE="$1"
if [ ! -f "$BACKUP_FILE" ]; then
  echo "Arquivo nao encontrado: $BACKUP_FILE"
  exit 1
fi

echo "Parando Jenkins Controller..."
docker compose stop jenkins-controller

echo "Restaurando backup $BACKUP_FILE para volume jenkins_home..."
docker run --rm \
  -v "${COMPOSE_PROJECT_NAME:-jenkins-platform}_jenkins_home:/data" \
  -v "$(cd "$(dirname "$BACKUP_FILE")" && pwd):/backup:ro" \
  alpine:3.20 \
  sh -c "rm -rf /data/* && tar -xzf /backup/$(basename "$BACKUP_FILE") -C /data"

echo "Subindo Jenkins Controller..."
docker compose up -d jenkins-controller

echo "Restore concluido."
