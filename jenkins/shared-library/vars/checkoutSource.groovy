def call(Map cfg = [:]) {
  String branch = cfg.get('branch', '')
  String credentialsId = cfg.get('credentialsId', '')

  if (cfg.get('scm', true)) {
    checkout scm
    return
  }

  if (!cfg.repoUrl) {
    error('checkoutSource exige repoUrl quando scm=false.')
  }

  def extensions = []
  if (cfg.get('cleanBeforeCheckout', true)) {
    extensions << [$class: 'CleanBeforeCheckout']
  }
  if (cfg.get('shallowClone', false)) {
    extensions << [$class: 'CloneOption', depth: cfg.get('depth', 20), shallow: true, noTags: false, timeout: 20]
  }

  def remoteConfig = [url: cfg.repoUrl]
  if (credentialsId?.trim()) {
    remoteConfig.credentialsId = credentialsId
  }

  checkout([
    $class: 'GitSCM',
    branches: [[name: branch?.trim() ? branch : '*/main']],
    doGenerateSubmoduleConfigurations: false,
    userRemoteConfigs: [remoteConfig],
    extensions: extensions
  ])
}
