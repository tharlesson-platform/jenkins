def call(Map cfg = [:], Closure body) {
  node(cfg.get('label', 'agent-kubernetes')) {
    timestamps {
      ansiColor('xterm') {
        try {
          validateRef(
            ref: cfg.get('ref', env.BRANCH_NAME ?: env.TAG_NAME ?: ''),
            releaseOnly: cfg.get('releaseOnly', false),
            allowedPrefixes: cfg.get('allowedPrefixes', [])
          )
          body()
          currentBuild.result = currentBuild.result ?: 'SUCCESS'
        } catch (err) {
          currentBuild.result = 'FAILURE'
          throw err
        } finally {
          notifyBuild(status: currentBuild.result, message: "${env.JOB_NAME} #${env.BUILD_NUMBER} => ${currentBuild.result}")
        }
      }
    }
  }
}
