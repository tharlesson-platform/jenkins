#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKUP_DIR="$ROOT_DIR/backups"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
BACKUP_FILE="$BACKUP_DIR/jenkins-home-$TIMESTAMP.tar.gz"

mkdir -p "$BACKUP_DIR"

echo "Criando backup do volume jenkins_home em $BACKUP_FILE"
docker run --rm \
  -v "${COMPOSE_PROJECT_NAME:-jenkins-platform}_jenkins_home:/data:ro" \
  -v "$BACKUP_DIR:/backup" \
  alpine:3.20 \
  sh -c "tar -czf /backup/$(basename "$BACKUP_FILE") -C /data ."

echo "Backup concluido: $BACKUP_FILE"
