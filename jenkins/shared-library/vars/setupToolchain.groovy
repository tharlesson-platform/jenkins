def call(Map cfg = [:], Closure body = null) {
  List<String> envList = []

  if (cfg.get('mavenTool')) {
    envList << "MAVEN_HOME=${tool cfg.mavenTool}"
    envList << 'PATH+MAVEN=${MAVEN_HOME}/bin'
  }
  if (cfg.get('nodeTool')) {
    envList << "NODEJS_HOME=${tool cfg.nodeTool}"
    envList << 'PATH+NODE=${NODEJS_HOME}/bin'
  }

  if (envList.isEmpty()) {
    if (body != null) {
      body()
    }
    return
  }

  withEnv(envList) {
    if (body != null) {
      body()
    }
  }
}
