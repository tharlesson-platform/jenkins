# Modelo de Multibranch

Use este modelo ao integrar um novo repositorio no fluxo multibranch do Jenkins.

## Configuracao recomendada

1. Tipo de job: `Multibranch Pipeline`
2. Fonte de branch: GitHub/GitLab/Bitbucket
3. Credenciais: token de leitura com privilegio minimo
4. Caminho do script:
   - `pipelines/ci/Jenkinsfile.<stack>.ci` para CI
   - `pipelines/cd/Jenkinsfile.<target>.cd` para CD
5. Estrategias de execucao:
   - executar pull requests
   - executar branches nomeadas (`main`, `release/*`, `hotfix/*`)
   - limpar branches obsoletas

## Politicas opcionais

- Suprimir trigger automatico para mudancas apenas de documentacao.
- Proteger jobs de CD de producao com aprovacao manual e regras por branch/tag de release.
- Manter pelo menos 30 builds e 20 conjuntos de artefatos.

