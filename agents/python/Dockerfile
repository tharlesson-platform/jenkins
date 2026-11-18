FROM jenkins/inbound-agent:latest-jdk21@sha256:25a3704f7de4d3d0becc441f5683a227556658df1473865ee94c5232355678ab

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
