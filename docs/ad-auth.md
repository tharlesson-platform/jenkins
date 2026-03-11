# Autenticacao Active Directory

## O que esta configurado

1. Autenticacao AD via JCasC (`jenkins/casc/security.yaml`).
2. Autorizacao Role Strategy mapeada por grupos do AD.
3. Usuario interno de contingencia (`JENKINS_ADMIN_USER`) para cenario de contingencia.

## Variaveis de ambiente obrigatorias

1. `AD_DOMAIN`
2. `AD_SERVERS`
3. `AD_BIND_DN`
4. `AD_BIND_PASSWORD`
5. `AD_GROUP_ADMINS`
6. `AD_GROUP_DEVELOPERS`
7. `AD_GROUP_READONLY`
8. `AD_GROUP_RELEASE_MANAGERS`

## Variaveis de ambiente opcionais (alinhamento com politica de diretorio)

1. `AD_SITE`
2. `AD_USER_SEARCH_BASE`
3. `AD_GROUP_SEARCH_BASE`
4. `AD_USER_SEARCH_FILTER`
5. `AD_GROUP_SEARCH_FILTER`

Nota: em algumas versoes, o plugin Active Directory do Jenkins expoe menos opcoes JCasC que o plugin LDAP generico. As variaveis de base/filtro de busca foram mantidas para consistencia de politica e futura migracao de modo LDAP.

## Mapeamento de grupos para papeis

1. `jenkins-admins` -> administracao total
2. `jenkins-developers` -> leitura/execucao para jobs autorizados
3. `jenkins-readonly` -> acesso somente leitura
4. `jenkins-release-managers` -> permissoes de execucao de release e implantacao

## Fluxo de configuracao

1. Preencher variaveis de AD no `.env`.
2. Subir ou reiniciar o controller.
3. Fazer login com uma conta AD de teste.
4. Validar mapeamento de papeis por grupo.
5. Manter o usuario local de contingencia protegido e rotacionado.

## Permissoes recomendadas para conta de servico AD

1. Permissao apenas de leitura para busca no diretorio.
2. Sem permissao de login interativo.
3. Politica de rotacao alinhada com a politica corporativa de senha.
4. Escopo de acesso limitado aos dominios/OUs necessarios.

## Diagnostico

1. Falha de autenticacao para todos os usuarios:
   - validar conectividade de `AD_SERVERS` a partir do controller
   - validar bind DN e senha
   - validar dominio e caminho de confianca
2. Usuario autentica, mas fica sem permissao:
   - validar nomes de grupos AD no `.env`
   - validar replicacao de membresia
   - validar atribuicoes Role Strategy no JCasC
3. Comportamento intermitente de login/grupo:
   - verificar latencia de replicacao AD/GC
   - reduzir TTL de cache, se necessario, para acelerar propagacao de membresia

## Recomendacoes de seguranca

1. Preferir caminho de transporte AD seguro (LDAPS/GC em rede confiavel).
2. Restringir escopo da conta de bind no AD.
3. Monitorar falhas de autenticacao em logs do Jenkins e do diretorio.
4. Manter credenciais de contingencia em cofre seguro, nunca em arquivo plano.



