def call(Map cfg = [:]) {
  if (!cfg.get('enabled', false)) {
    echo '[runSonarScan] Scan Sonar desabilitado.'
    return
  }

  String scannerHome = tool cfg.get('scannerTool', 'SonarScanner-CLI')
  String serverName = cfg.get('serverName', 'sonarqube')
  String command = cfg.get('command', "${scannerHome}/bin/sonar-scanner")
  String containerImage = (cfg.get('containerImage', '') as String).trim()
  List envVars = (cfg.get('envVars', []) as List)

  withSonarQubeEnv(serverName) {
    if (containerImage) {
      runInBaseContainer(
        image: containerImage,
        command: command,
        envVars: envVars,
        label: 'Scan SonarQube em container base'
      )
    } else {
      sh label: 'Scan SonarQube', script: command
    }
  }

  if (cfg.get('waitForQualityGate', false)) {
    timeout(time: cfg.get('qualityGateTimeoutMinutes', 10), unit: 'MINUTES') {
      def qg = waitForQualityGate()
      if (qg.status != 'OK' && cfg.get('failOnQualityGate', true)) {
        error("Quality Gate falhou: ${qg.status}")
      }
      echo "[runSonarScan] Status do Quality Gate: ${qg.status}"
    }
  }
}
