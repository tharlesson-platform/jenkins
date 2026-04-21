#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if ! docker compose ps --status running jenkins-controller >/dev/null 2>&1; then
  echo "Jenkins Controller nao esta em execucao. Suba o servico antes da auditoria."
  exit 1
fi

echo "Plugins instalados no container:"
docker compose exec -T jenkins-controller sh -c "ls -1 /var/jenkins_home/plugins | sed 's/\\.jpi$//;s/\\.hpi$//' | sort -u"

echo
echo "Plugins declarados em jenkins/plugins.txt:"
grep -vE '^\s*#|^\s*$' jenkins/plugins.txt
