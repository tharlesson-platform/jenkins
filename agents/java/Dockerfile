ARG INBOUND_AGENT_TAG=latest-jdk21
FROM jenkins/inbound-agent:${INBOUND_AGENT_TAG}

USER root

ARG GRADLE_VERSION=8.10.2

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    git \
    jq \
    maven \
    unzip \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o /tmp/gradle.zip \
    && unzip -q /tmp/gradle.zip -d /opt \
    && ln -s "/opt/gradle-${GRADLE_VERSION}/bin/gradle" /usr/local/bin/gradle \
    && rm -f /tmp/gradle.zip

COPY agents/base/register-agent.sh /usr/local/bin/register-agent.sh
RUN chmod 0755 /usr/local/bin/register-agent.sh

ENV GRADLE_HOME="/opt/gradle-${GRADLE_VERSION}"
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

USER jenkins
ENTRYPOINT ["/usr/local/bin/register-agent.sh"]
