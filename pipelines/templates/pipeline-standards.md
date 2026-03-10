# Padroes de Pipeline

## Estagios obrigatorios (CI)

1. Coleta do codigo
2. Compilacao
3. Testes unitarios
4. Publicacao de relatorios de testes
5. Verificacoes de seguranca
6. Publicacao de artefato/imagem

## Estagios obrigatorios (CD)

1. Validacao
2. Aprovacao (opcional, porem recomendada para stage/prod)
3. Implantacao
4. Verificacao apos implantacao
5. Caminho de reversao

## Regras operacionais

- Sempre usar labels explicitas de agent.
- Manter segredos em credentials/env vars; nunca hardcode.
- Publicar relatorios estruturados (JUnit, cobertura, relatorios de analise).
- Usar `timeout` para chamadas externas de longa duracao.
- Garantir parametro/estrategia de reversao em todo pipeline de CD.
- Garantir logs com ambiente, versao e alvo de forma clara.
