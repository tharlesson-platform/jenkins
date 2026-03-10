def call(Map cfg = [:]) {
  String status = cfg.get('status', currentBuild.currentResult ?: 'UNKNOWN')
  String channel = cfg.get('channel', '')
  String message = cfg.get('message', "${env.JOB_NAME} #${env.BUILD_NUMBER} -> ${status}")

  echo "[notifyBuild] ${message}"

  if (channel?.trim() && cfg.get('slackEnabled', false)) {
    try {
      slackSend(channel: channel, message: message)
    } catch (ignored) {
      echo '[notifyBuild] Plugin Slack nao configurado; ignorando.'
    }
  }
}
