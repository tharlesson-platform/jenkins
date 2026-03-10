# Backup e Restauracao

## Escopo

Alvo principal de backup:

1. Volume persistente do Jenkins: `jenkins_home`

Tambem manter versionados os arquivos do repositorio (Git):

1. JCasC e scripts de inicializacao
2. Definicoes de pipeline
3. biblioteca compartilhada e seed jobs
4. Scripts operacionais e documentacao

## Procedimento de backup

Comando:

```bash
./scripts/backup-jenkins.sh
```

Saida:

1. Arquivo em `./backups/jenkins-home-<timestamp>.tar.gz`

## Procedimento de restauracao

Comando:

```bash
./scripts/restore-jenkins.sh ./backups/<arquivo-backup>.tar.gz
```

Comportamento:

1. Para o controller.
2. Substitui o conteudo de `jenkins_home` pelo arquivo.
3. Sobe o controller novamente.

## Guardrails operacionais

1. Nao executar restauracao durante implantacoes criticas.
2. Sempre capturar backup atual antes de restauracao.
3. Validar checksum do arquivo de backup antes da restauracao.
4. Testar restauracao em staging pelo menos uma vez por ciclo de release.

## Politica sugerida de retencao

1. Backups diarios: manter 14 dias.
2. Backups semanais: manter 8 semanas.
3. Backups mensais: manter 6-12 meses (conforme compliance).

## Checklist de validacao apos restauracao

1. Controller saudavel (`/login`).
2. Seed jobs e pastas principais existem.
3. Credenciais estao legiveis e utilizaveis.
4. Pelo menos um pipeline CI de exemplo executa com sucesso.
5. Autenticacao AD e RBAC continuam funcionando.

## Notas de recuperacao de desastre

1. Metas de RPO/RTO devem ser definidas pelo time de operacoes.
2. Armazenar copias de backup fora do servidor local.
3. Em producao, preferir armazenamento criptografado e logs de auditoria de acesso.
4. Se Sonar/Nexus forem criticos para o negocio, aplicar politica de backup independente para os volumes deles.



