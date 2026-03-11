ARG INBOUND_AGENT_TAG=latest-jdk21
FROM jenkins/inbound-agent:${INBOUND_AGENT_TAG}

USER root

RUN apt-get update && apt-get install -y --no-install-recommends \
    bash \
    ca-certificates \
    curl \
    git \
    jq \
    unzip \
    && rm -rf /var/lib/apt/lists/*

COPY agents/base/register-agent.sh /usr/local/bin/register-agent.sh
RUN chmod 0755 /usr/local/bin/register-agent.sh

USER jenkins
ENTRYPOINT ["/usr/local/bin/register-agent.sh"]
