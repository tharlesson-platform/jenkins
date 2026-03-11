import org.company.devops.PipelineUtils

def call(Map cfg = [:]) {
  String ref = cfg.get('ref', env.BRANCH_NAME ?: env.TAG_NAME ?: '')
  boolean releaseOnly = cfg.get('releaseOnly', false)
  List<String> allowedPrefixes = cfg.get('allowedPrefixes', [])

  if (releaseOnly && !PipelineUtils.isReleaseRef(ref)) {
    error("Ref '${ref}' nao e permitida para pipeline de release.")
  }

  if (allowedPrefixes && !allowedPrefixes.any { prefix -> ref?.startsWith(prefix) }) {
    error("Ref '${ref}' nao e permitida. Prefixos permitidos: ${allowedPrefixes.join(', ')}")
  }

  echo "[validateRef] Ref '${ref}' aceita."
}
