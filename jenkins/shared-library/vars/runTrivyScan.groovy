def call(Map cfg = [:]) {
  String image = cfg.get('image', '')
  if (!image?.trim()) {
    error('runTrivyScan exige nome da imagem.')
  }

  String severity = cfg.get('severity', env.TRIVY_SEVERITY ?: 'HIGH,CRITICAL')
  String timeout = cfg.get('timeout', env.TRIVY_TIMEOUT ?: '10m')
  String reportFile = cfg.get('reportFile', 'trivy-report.json')
  boolean ignoreUnfixed = cfg.get('ignoreUnfixed', (env.TRIVY_IGNORE_UNFIXED ?: 'true').toBoolean())
  boolean failOnVuln = cfg.get('failOnVuln', true)

  String ignoreFlag = ignoreUnfixed ? '--ignore-unfixed' : ''
  String exitCode = failOnVuln ? '1' : '0'
  String format = cfg.get('format', 'json')

  sh label: 'Scan de imagem com Trivy', script: """
    set -e
    trivy image \\
      --timeout ${timeout} \\
      --severity ${severity} \\
      --format ${format} \\
      --output ${reportFile} \\
      --exit-code ${exitCode} \\
      ${ignoreFlag} \\
      ${image}
  """

  archiveArtifacts artifacts: reportFile, onlyIfSuccessful: false, allowEmptyArchive: false
}
