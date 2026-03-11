ARG INBOUND_AGENT_TAG=latest-jdk21
FROM jenkins/inbound-agent:${INBOUND_AGENT_TAG}

USER root

ARG GO_VERSION=1.24.1
ARG GOLANGCI_LINT_VERSION=v1.61.0

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    git \
    jq \
    tar \
    && curl -fsSL "https://go.dev/dl/go${GO_VERSION}.linux-amd64.tar.gz" -o /tmp/go.tgz \
    && rm -rf /usr/local/go \
    && tar -C /usr/local -xzf /tmp/go.tgz \
    && rm -f /tmp/go.tgz \
    && curl -sSfL https://raw.githubusercontent.com/golangci/golangci-lint/master/install.sh \
      | sh -s -- -b /usr/local/bin "${GOLANGCI_LINT_VERSION}" \
    && rm -rf /var/lib/apt/lists/*

COPY agents/base/register-agent.sh /usr/local/bin/register-agent.sh
RUN chmod 0755 /usr/local/bin/register-agent.sh

ENV GOROOT="/usr/local/go"
ENV PATH="${GOROOT}/bin:${PATH}"

USER jenkins
ENTRYPOINT ["/usr/local/bin/register-agent.sh"]
