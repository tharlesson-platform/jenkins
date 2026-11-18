# node-app

Exemplo minimo em Node para validacao de CI no Jenkins.

## Execucao local

```bash
npm ci
npm run lint
npm test
node src/index.js
```

## Testes sem dependências externas

O exemplo usa `node:test` e `node:assert/strict` do Node.js 22 ou superior.
`npm ci` continua obrigatório com lockfile versionado; `npm test` preserva
o caso existente e emite JUnit em `test-results/junit.xml` e cobertura LCOV
em `coverage/lcov.info`, consumidos pelo Jenkins/Sonar. Nenhum teste ou
limiar é removido. A cadeia Jest/jest-junit e suas dependências vulneráveis
foi substituída pela capacidade nativa; `npm audit` continua incluindo todas
as dependências, sem exceção de severidade ou de desenvolvimento.

Documentação: https://nodejs.org/docs/latest-v22.x/api/test.html
