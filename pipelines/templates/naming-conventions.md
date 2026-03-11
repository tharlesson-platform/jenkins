# Convencoes de Nomenclatura

## Convencoes de pasta

- `CI/Java/<time-ou-app>`
- `CI/Python/<time-ou-app>`
- `CI/Node/<time-ou-app>`
- `CI/Go/<time-ou-app>`
- `CI/Containers/<time-ou-app>`
- `CD/Apps/<time-ou-app>/<alvo>`

## Nomes de jobs de pipeline

- CI: `ci-<stack>-<app>`
- CD: `cd-<target>-<app>`
- Admin: `admin-<finalidade>`

## Convencoes de branch e tag

- `main` para integracao padrao
- `release/<version>` para estabilizacao de release
- `hotfix/<ticket>` para correcoes urgentes
- tags: `v<major>.<minor>.<patch>`

## Tags de artefato e imagem

- tag de branch: nome saneado da branch
- tag imutavel: commit SHA curto
- tag de release: tag semantica quando existir
