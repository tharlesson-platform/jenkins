ARG JENKINS_VERSION=lts-jdk21
FROM jenkins/jenkins:${JENKINS_VERSION}

USER root

# Ferramentas utilitarias no controller (evitar builds pesados aqui; usar agents para execucao)
RUN apt-get update && apt-get install -y --no-install-recommends \
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
RUN jenkins-plugin-cli --verbose --plugin-file /usr/share/jenkins/ref/plugins.txt

# Bootstrap as code
COPY casc/ /usr/share/jenkins/ref/casc/
COPY init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
COPY seed-jobs/ /usr/share/jenkins/ref/seed-jobs/
COPY shared-library/ /usr/share/jenkins/ref/shared-library/

ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc
