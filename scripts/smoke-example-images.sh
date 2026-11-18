#!/usr/bin/env bash
# Exemplos CLI somente no Docker rootless da VM descartável admitida.
set -euo pipefail
root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"
test -f /opt/isolation-base-ready
docker info --format '{{range .SecurityOptions}}{{println .}}{{end}}' | grep -Fx name=rootless
maven_image='maven:3.9.13-eclipse-temurin-25@sha256:ade3c87e3cdfbe04932afa16b31814cbf60b0122d21d78a76530684a1eeb7cc2'
node_image='node:25-alpine@sha256:bdf2cca6fe3dabd014ea60163eca3f0f7015fbd5c7ee1b0e9ccb4ced6eb02ef4'
docker run --rm -v "$PWD/examples/node-app:/app" -w /app "$node_image" \
 sh -ec 'npm ci; npm run lint; npm test; npm run build; npm audit --json > test-results/npm-audit.json; test -s test-results/junit.xml; test -s coverage/lcov.info'
python3 - <<'PY'
import json
from pathlib import Path
import xml.etree.ElementTree as ET
root=Path('examples/node-app')
report=json.loads((root/'test-results/npm-audit.json').read_text())
if report['metadata']['vulnerabilities']['total'] != 0:
    raise SystemExit('npm audit reportou vulnerabilidades')
ET.parse(root/'test-results/junit.xml')
if 'SF:' not in (root/'coverage/lcov.info').read_text():
    raise SystemExit('LCOV ausente ou inválido')
PY
docker run --rm -v "$PWD/examples/java-maven-app:/app" -w /app "$maven_image" mvn -q package
for example in python-app node-app java-maven-app go-app; do
 tag="jenkins-example-ci:${example}"
 docker build --tag "$tag" "examples/$example"
 runtime_uid=$(docker run --rm --entrypoint id "$tag" -u)
 test "$runtime_uid" = 10001
 output=$(docker run --rm --read-only --tmpfs /tmp:rw,nosuid,nodev,size=64m \
   --cap-drop ALL --security-opt no-new-privileges "$tag")
 case "$example" in
  python-app) expected='python-app ready' ;;
  node-app) expected='node-app ready: 2 + 3 = 5' ;;
  java-maven-app) expected='java-maven-app ready' ;;
  go-app) expected='go-app ready' ;;
  *) echo "Exemplo desconhecido: $example" >&2; exit 1 ;;
 esac
 if [[ "$output" != *"$expected"* ]]; then
  echo "Saída inesperada no exemplo $example" >&2
  exit 1
 fi
 echo "Exemplo $example executado sem root com sucesso."
done
# Não aceitar binário amd64 dentro de uma imagem declarada ARM64 (nem via emulação).
go_arch=$(docker image inspect jenkins-example-ci:go-app --format '{{.Architecture}}')
go_elf=$(docker run --rm --entrypoint sh jenkins-example-ci:go-app -c 'od -An -tu2 -j18 -N2 /app' | tr -d '[:space:]')
case "$go_arch:$go_elf" in arm64:183|amd64:62) ;; *) echo 'Arquitetura Go inconsistente.' >&2; exit 1 ;; esac
