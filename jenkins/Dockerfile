ARG JENKINS_VERSION=lts-jdk21
FROM jenkins/jenkins:${JENKINS_VERSION}

USER root

# Ferramentas utilitarias no controller (evitar builds pesados aqui; usar agents para execucao)
# Official Debian mirrors use HTTPS through the isolated CI egress proxy.
# Keep APT signature verification and require the base image's trusted CA bundle.
RUN set -eu; \
    test -s /etc/ssl/certs/ca-certificates.crt; \
    for source in /etc/apt/sources.list /etc/apt/sources.list.d/*.list /etc/apt/sources.list.d/*.sources; do \
      [ ! -f "$source" ] || sed -i \
        -e 's|http://deb\.debian\.org/|https://deb.debian.org/|g' \
        -e 's|http://security\.debian\.org/|https://security.debian.org/|g' "$source"; \
    done; \
    apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    git \
    jq \
    unzip \
    gnupg \
    lsb-release \
    docker.io \
    && rm -rf /var/lib/apt/lists/*

# Trivy CLI para scans pontuais e suporte a pipelines simples.
RUN curl -fsSL https://aquasecurity.github.io/trivy-repo/deb/public.key \
    | gpg --dearmor -o /usr/share/keyrings/trivy.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/trivy.gpg] https://aquasecurity.github.io/trivy-repo/deb generic main" \
      > /etc/apt/sources.list.d/trivy.list \
    && apt-get update && apt-get install -y --no-install-recommends trivy \
    && rm -rf /var/lib/apt/lists/*

USER jenkins

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
# Use the official stable archive for this build; retain plugin integrity checks.
ARG JENKINS_PLUGIN_JAVA_OPTS=""
RUN JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-}${JENKINS_PLUGIN_JAVA_OPTS:+ ${JENKINS_PLUGIN_JAVA_OPTS}}" \
    JENKINS_UC_DOWNLOAD=https://archives.jenkins.io \
    jenkins-plugin-cli --verbose --plugin-file /usr/share/jenkins/ref/plugins.txt

# Bootstrap as code
COPY casc/ /usr/share/jenkins/ref/casc/
COPY init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
COPY seed-jobs/ /usr/share/jenkins/ref/seed-jobs/
COPY shared-library/ /usr/share/jenkins/ref/shared-library/

ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc
