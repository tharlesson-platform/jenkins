# Integracao de Times

## Objetivos da integracao

1. Trazer um novo time para CI/CD com seguranca e rapidez.
2. Reutilizar Jenkinsfiles padrao e padroes da biblioteca compartilhada.
3. Evitar deriva de configuracao manual pontual.

## Fluxo de integracao de novo projeto

1. Classificar tecnologia:
   - Java
   - Python
   - Node
   - Go
   - Apenas container
2. Escolher trilha de pipeline:
   - pipeline padrao semeada (`*-ci`)
   - pipeline multibranch (`*-mb`)
3. Registrar credenciais necessarias:
   - credenciais de SCM
   - credenciais de Nexus/registro
   - credenciais de implantacao quando CD estiver habilitado
4. Confirmar repositorio de destino de artefato no Nexus.
5. Rodar pipeline inicial em branch nao produtiva.
6. Habilitar CD somente apos linha base de CI estavel.

## Convencoes de pasta e nome

1. CI:
   - `CI/<Stack>/<app-ou-time>`
2. CD:
   - `CD/Apps/<app-ou-time>/<alvo>`
3. Padrao completo em:
   - `pipelines/templates/naming-conventions.md`

## Checklist de novo time

1. Grupos AD do time mapeados e validados.
2. Seed job executado com sucesso.
3. Label de agent escolhida corretamente.
4. Pelo menos uma execucao CI bem-sucedida.
5. Analises de seguranca habilitadas (ou excecoes documentadas).
6. Publicacao de artefato validada.
7. Estrategia de parametro de reversao documentada para CD.

## Fluxo para integracao de novo agent

1. Criar Dockerfile em `agents/<novo-agent>/`.
2. Adicionar servico/perfil no compose.
3. Adicionar definicao de node em `jenkins/casc/nodes.yaml`.
4. Adicionar mapeamento de labels em `docs/pipelines.md`.
5. Reconstruir e validar status online.

## Fluxo para integracao de novo Jenkinsfile

1. Adicionar Jenkinsfile em `pipelines/ci` ou `pipelines/cd`.
2. Reutilizar etapas da biblioteca compartilhada antes de logica customizada.
3. Adicionar job semeado em `jenkins/seed-jobs/jobdsl/base.groovy`.
4. Executar `seed-local-files`.
5. Validar com repositorio de exemplo ou homologacao.

## Checklist operacional da primeira semana

1. Login AD e permissoes de papel validados para usuarios do time.
2. Notificacoes de pipeline configuradas (Slack/email, se usado).
3. Backup e restauracao testados uma vez.
4. Auditoria de plugins executada.
5. Procedimentos de upgrade e reversao compreendidos pelos lideres do time.
