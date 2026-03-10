def call(Map cfg = [:]) {
  String type = cfg.get('type', 'raw')
  String credentialsId = cfg.get('credentialsId', 'nexus-ci')
  String nexusUrl = cfg.get('nexusUrl', env.NEXUS_URL ?: 'http://nexus:8081')
  String repository = cfg.get('repository', '')

  if (!repository?.trim()) {
    error('publishArtifact exige nome de repositorio.')
  }

  switch (type) {
    case 'maven':
      sh "mvn -B -ntp -DskipTests deploy -DaltDeploymentRepository=${repository}::default::${nexusUrl}/repository/${repository}/"
      break
    case 'npm':
      withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
        sh """
          set -e
          npm config set registry ${nexusUrl}/repository/${repository}/
          npm config set //${nexusUrl.replaceFirst(/^https?:\\/\\//, '')}/repository/${repository}/:_auth \$(printf "%s:%s" "$NEXUS_USER" "$NEXUS_PASS" | base64 -w 0)
          npm publish --registry ${nexusUrl}/repository/${repository}/
        """
      }
      break
    case 'pypi':
      withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'TWINE_USERNAME', passwordVariable: 'TWINE_PASSWORD')]) {
        sh """
          set -e
          python -m pip install --quiet twine
          python -m twine upload --repository-url ${nexusUrl}/repository/${repository}/ dist/*
        """
      }
      break
    default:
      String file = cfg.get('file', '')
      if (!file?.trim()) {
        error('publishArtifact type=raw exige caminho de arquivo.')
      }
      withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
        sh """
          set -e
          curl -fsS -u "$NEXUS_USER:$NEXUS_PASS" \\
            --upload-file "${file}" \\
            "${nexusUrl}/repository/${repository}/${cfg.get('targetPath', file.tokenize('/').last())}"
        """
      }
  }
}
