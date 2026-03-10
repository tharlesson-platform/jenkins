def call(Map cfg = [:]) {
  String image = (cfg.get('image', '') as String).trim()
  if (!image) {
    error('runInBaseContainer exige parametro image.')
  }

  String command = (cfg.get('command', '') as String).trim()
  if (!command) {
    error('runInBaseContainer exige parametro command.')
  }

  String workdir = (cfg.get('workdir', '/workspace') as String).trim()
  String shell = (cfg.get('shell', '/bin/sh') as String).trim()
  String containerArgs = (cfg.get('containerArgs', '') as String).trim()
  boolean pull = cfg.get('pull', true) as boolean
  List envVars = (cfg.get('envVars', []) as List)

  String envFlags = envVars
    .collect { it?.toString()?.trim() }
    .findAll { it }
    .collect { "-e ${it}" }
    .join(' ')

  String escapedCommand = command.replace("'", "'\"'\"'")
  String label = (cfg.get('label', "Executar em container base ${image}") as String)

  sh label: label, script: """
    set -eu
    ${pull ? "docker pull '${image}' >/dev/null 2>&1 || true" : ":"}
    cid=\$(docker create ${containerArgs} ${envFlags} --workdir '${workdir}' '${image}' ${shell} -lc '${escapedCommand}')
    cleanup() { docker rm -f \"\$cid\" >/dev/null 2>&1 || true; }
    trap cleanup EXIT

    docker cp . \"\$cid\":'${workdir}'
    docker start -a \"\$cid\"
    docker cp \"\$cid\":'${workdir}'/. .

    cleanup
    trap - EXIT
  """
}
