def call(Map cfg = [:], Closure body) {
  node(cfg.get('label', 'agent-docker')) {
    timestamps {
      ansiColor('xterm') {
        try {
          stage('Coleta do Codigo') {
            checkoutSource(cfg.get('checkout', [scm: true]))
          }
          body()
          currentBuild.result = currentBuild.result ?: 'SUCCESS'
        } catch (err) {
          currentBuild.result = 'FAILURE'
          throw err
        } finally {
          if (cfg.get('cleanWs', true)) {
            cleanWs(cleanWhenNotBuilt: false, deleteDirs: true, disableDeferredWipeout: true)
          }
          notifyBuild(status: currentBuild.result, message: "${env.JOB_NAME} #${env.BUILD_NUMBER} => ${currentBuild.result}")
        }
      }
    }
  }
}
