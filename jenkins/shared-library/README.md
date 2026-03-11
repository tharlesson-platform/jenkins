# Biblioteca Compartilhada Jenkins

Funcoes reutilizaveis de pipeline para padronizacao de CI/CD.

## Etapas principais

- `checkoutSource`
- `runInBaseContainer`
- `setupToolchain`
- `publishJUnit`
- `runSonarScan`
- `runTrivyScan`
- `publishArtifact`
- `notifyBuild`
- `validateRef`
- `ciPipeline`
- `cdPipeline`

## Uso

```groovy
@Library('jenkins-shared-lib') _
```
