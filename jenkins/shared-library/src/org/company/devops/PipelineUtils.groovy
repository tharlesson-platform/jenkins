package org.company.devops

class PipelineUtils implements Serializable {
  static boolean envAsBool(def script, String key, boolean defaultValue = false) {
    String raw = script.env[key]
    if (raw == null || raw.trim().isEmpty()) {
      return defaultValue
    }
    String normalized = raw.trim().toLowerCase()
    return ['1', 'true', 'yes', 'y', 'on'].contains(normalized)
  }

  static String sanitizeTag(String value, String fallback = 'latest') {
    if (value == null || value.trim().isEmpty()) {
      return fallback
    }
    return value.trim().toLowerCase()
      .replaceAll('[^a-z0-9._-]', '-')
      .replaceAll('-+', '-')
  }

  static boolean isReleaseRef(String branchOrTag) {
    if (branchOrTag == null || branchOrTag.trim().isEmpty()) {
      return false
    }
    return branchOrTag ==~ /^main$/ ||
      branchOrTag ==~ /^master$/ ||
      branchOrTag ==~ /^release\/.+$/ ||
      branchOrTag ==~ /^hotfix\/.+$/ ||
      branchOrTag ==~ /^v\d+\.\d+\.\d+.*$/
  }

  static void requireEnv(def script, List<String> keys) {
    List<String> missing = keys.findAll { key ->
      String value = script.env[key]
      value == null || value.trim().isEmpty()
    }
    if (!missing.isEmpty()) {
      script.error("Variaveis de ambiente obrigatorias ausentes: ${missing.join(', ')}")
    }
  }
}
