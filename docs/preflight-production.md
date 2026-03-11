# Preflight de Producao

Use este guia antes de promover a plataforma Jenkins para producao.

## Como usar

1. Complete cada etapa na ordem.
2. Marque cada etapa como PASS ou FAIL com evidencias.
3. Se qualquer etapa obrigatoria falhar, declare NO-GO.
4. Guarde o checklist preenchido com os registros de mudanca/release.

## Etapa 1: Infraestrutura e capacidade

Verificacoes obrigatorias:

1. Dimensionamento de servidor validado para concorrencia esperada.
2. Volumes persistentes montados e gravaveis.
3. Rotas de rede validadas para:
   - interface Jenkins
   - servidores AD
   - Nexus
   - SonarQube
   - alvos de implantacao (VM/K8s/ECS)
4. Versoes de Docker/Compose suportadas.

Comandos de evidencia:

```bash
docker version
docker compose version
docker compose config >/dev/null
docker compose ps
```

## Etapa 2: Base de seguranca

Verificacoes obrigatorias:

1. Sem senhas/segredos padrao em `.env`.
2. Conta de bind AD com privilegio minimo e rotacao.
3. Acesso admin anonimo desabilitado.
4. Papeis RBAC mapeados corretamente para grupos AD.
5. Estrategia de terminacao TLS definida (proxy/ingress).
6. Inventario de credenciais validado no Jenkins.

Evidencias:

1. Revisao de `jenkins/casc/security.yaml`.
2. Login de teste bem-sucedido com cada perfil:
   - admin
   - desenvolvedor
   - somente-leitura
   - gestor-de-release

## Etapa 3: Saude da plataforma e inicializacao

Verificacoes obrigatorias:

1. Controller saudavel.
2. Agents obrigatorios online.
3. SonarQube saudavel (se habilitado).
4. Nexus saudavel (se habilitado).
5. Seed jobs executam com sucesso.

Comandos de evidencia:

```bash
./scripts/smoke-tests.sh
docker compose logs --tail=200 jenkins-controller
```

## Etapa 4: Governanca de plugins e configuracao

Verificacoes obrigatorias:

1. Conjunto de plugins revisado e aprovado.
2. Auditoria de plugins executada e arquivada.
3. Arquivos JCasC versionados e revisados por pares.
4. Tag da imagem do controller e versao Jenkins LTS fixadas para release.

Comandos de evidencia:

```bash
./scripts/plugin-audit.sh
grep -vE '^\s*#|^\s*$' jenkins/plugins.txt
```

## Etapa 5: Prontidao de backup e restauracao

Verificacoes obrigatorias:

1. Backup executado com sucesso.
2. Restore testada fora de producao.
3. Politica de retencao documentada.
4. Armazenamento de backup fora do servidor configurado.

Comandos de evidencia:

```bash
./scripts/backup-jenkins.sh
ls -lh backups/
```

## Etapa 6: Validacao de controles de CI

Verificacoes obrigatorias:

1. Pelo menos um CI Java em verde.
2. Pelo menos um CI nao Java em verde.
3. Relatorios de scan de seguranca arquivados (dependencia/imagem).
4. Publicacao de artefato no Nexus validada.

Jobs sugeridos:

1. `CI/Java/java-maven-app-ci`
2. `CI/Python/python-app-ci` ou `CI/Node/node-app-ci` ou `CI/Go/go-app-ci`
3. `CI/Containers/container-image-ci`

## Etapa 7: Validacao de controles de CD

Verificacoes obrigatorias:

1. Etapa de aprovacao testada para stage/prod.
2. Caminho de comando de implantacao verificado no ambiente alvo.
3. Parametro de reversao testada com sucesso.
4. Log de auditoria de eventos de implantacao retido.

Jobs sugeridos:

1. `CD/Apps/docker-cd`
2. `CD/Apps/k8s-cd`
3. `CD/Apps/ecs-cd`
4. `CD/Apps/vm-cd`

## Etapa 8: Observabilidade e operacao

Verificacoes obrigatorias:

1. Caminho de coleta de logs definido.
2. Caminho de alerta definido para:
   - falha de saude de servico
   - falha de backup
   - falha critica de pipeline
3. Responsavel de plantao e escalacao documentados.
4. Guias vinculados para resposta e recuperacao de incidente.

## Etapa 9: Janela de mudanca e plano de reversao

Verificacoes obrigatorias:

1. Janela de release aprovada.
2. Criterios de gatilho para reversao definidos.
3. Dono da reversao definido.
4. Release anterior conhecida e funcional disponivel.

## Modelo de decisao GO / NO-GO

Registrar:

1. Data/hora da release (fuso local).
2. Dono da mudanca.
3. Revisores.
4. Resumo de resultados por etapa.
5. Decisao final:
   - GO
   - NO-GO
6. Se NO-GO: etapas bloqueantes e plano de remediacao.

## Pacote rapido de comandos

```bash
cp .env.example .env
docker compose config >/dev/null
docker compose up -d --build jenkins-controller
docker compose --profile agents --profile sonar --profile nexus up -d --build
./scripts/smoke-tests.sh
./scripts/plugin-audit.sh
./scripts/backup-jenkins.sh
```

## Automacao opcional

Execute:

```bash
./scripts/preflight-production.sh
```

Este script executa verificacoes de linha base e retorna codigo diferente de zero em falhas criticas.







