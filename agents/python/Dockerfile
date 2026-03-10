ARG INBOUND_AGENT_TAG=latest-jdk21
FROM jenkins/inbound-agent:${INBOUND_AGENT_TAG}

USER root

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    git \
    jq \
    python3 \
    python3-pip \
    python3-venv \
    && ln -sf /usr/bin/python3 /usr/local/bin/python \
    && python3 -m pip install --no-cache-dir --break-system-packages \
      virtualenv \
      pytest \
      coverage \
      bandit \
      pip-audit \
    && rm -rf /var/lib/apt/lists/*

COPY agents/base/register-agent.sh /usr/local/bin/register-agent.sh
RUN chmod 0755 /usr/local/bin/register-agent.sh

USER jenkins
ENTRYPOINT ["/usr/local/bin/register-agent.sh"]
