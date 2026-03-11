# Seguranca

## Base de seguranca deste repositorio

1. Autenticacao via Active Directory em JCasC.
2. Autorizacao via Role Strategy com mapeamento de grupos AD.
3. Sem acesso anonimo administrativo por padrao.
4. Credenciais e segredos externalizados por variaveis e Jenkins Credentials.
5. Analises de seguranca nas pipelines (dependencias e imagens).
6. Verificacoes de saude de componentes criticos (controller, SonarQube e Nexus).

## Modelo de identidade e acesso

1. `jenkins-admins`: administracao total.
2. `jenkins-developers`: leitura + execucao + acoes limitadas de execucao.
3. `jenkins-readonly`: visibilidade somente leitura.
4. `jenkins-release-managers`: permissao de release e implantacao.

## Politica de segredos

1. Nunca comitar segredo real em Git.
2. Usar `.env.example` apenas como modelo.
3. Usar `jenkins/casc/credentials.example.yaml` como referencia estrutural.
4. Rotacionar antes de producao:
   - senha local de contingencia do Jenkins
   - senha de bind do AD
   - credenciais de servico Nexus
   - credenciais de registry
   - credenciais AWS e SSH de implantacao
5. Migracao recomendada para producao:
   - Vault
   - AWS Secrets Manager
   - Azure Key Vault
   - GCP Secret Manager

## Endurecimento de controller e agents

1. Manter executores do controller em `0` para papel de controle.
2. Restringir labels de agent e evitar `agent any` em jobs de producao.
3. Evitar uso irrestrito de Docker socket quando possivel.
4. Fixar imagens base confiaveis para controller e agents.
5. Atualizar regularmente imagens dos agents e validar CVEs.

## Endurecimento de rede

1. Expor UI do Jenkins apenas por caminho de rede controlado.
2. Restringir/firewall da porta de agent quando so WebSocket for usado.
3. Usar TLS na borda (proxy/ingress).
4. Manter SonarQube e Nexus internos por padrao.
5. Limitar egress de agents em ambientes regulados.

## Controles de seguranca de pipeline

1. Analise de dependencias:
   - Java: dependency-check
   - Python: pip-audit
2. SAST:
   - Python: bandit
   - Go: golangci-lint (analise estatica/lint)
3. Analise de imagem:
   - Trivy com politica de falha configuravel (`HIGH,CRITICAL` por padrao)
4. Etapa opcional de qualidade:
   - SonarQube com opcao de esperar/falhar por parametro

## Governanca de plugins

1. Inventario de plugins em `jenkins/plugins.txt`.
2. Rodar auditoria antes e depois de upgrades.
3. Politica de atualizacao:
   - validar em ambiente nao produtivo
   - manter registro de mudanca
   - promover so apos pipeline de fumaca
4. Evitar plugins desnecessarios para reduzir superficie de ataque.

## Recomendacoes de auditoria e compliance

1. Habilitar centralizacao de logs do execucao de containers.
2. Registrar mudancas administrativas preferencialmente via codigo.
3. Alinhar retencao de backup com exigencias regulatarias.
4. Revisar periodicamente grupos AD e papeis Jenkins.
5. Adicionar etapa de promocao com assinatura/atestado de artefatos em producao.

## Guia rapido de resposta a incidente

1. Isolar:
   - pausar pipelines afetadas
   - desabilitar credenciais comprometidas
2. Conter:
   - rotacionar segredos
   - bloquear jobs/labels suspeitos
3. Recuperar:
   - restaurar backup confiavel, se necessario
   - executar verificacoes de fumaca novamente
4. Aprender:
   - atualizar guias e controles
   - reforcar deteccao nas etapas de seguranca

