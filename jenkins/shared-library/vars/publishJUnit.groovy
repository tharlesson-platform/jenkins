def call(Map cfg = [:]) {
  String reportPattern = cfg.get('pattern', '**/test-results/**/*.xml,**/surefire-reports/*.xml')
  junit allowEmptyResults: cfg.get('allowEmptyResults', true),
        testResults: reportPattern,
        keepLongStdio: cfg.get('keepLongStdio', true)
}
