ARG INBOUND_AGENT_TAG=latest-jdk21
FROM jenkins/inbound-agent:${INBOUND_AGENT_TAG}

USER root

ARG KUBECTL_VERSION=v1.31.3
ARG HELM_VERSION=v3.16.3
ARG YQ_VERSION=v4.44.5
ARG INSTALL_AWSCLI=false

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    git \
    jq \
    unzip \
    && curl -fsSL "https://dl.k8s.io/release/${KUBECTL_VERSION}/bin/linux/amd64/kubectl" \
      -o /usr/local/bin/kubectl \
    && chmod 0755 /usr/local/bin/kubectl \
    && curl -fsSL "https://get.helm.sh/helm-${HELM_VERSION}-linux-amd64.tar.gz" -o /tmp/helm.tgz \
    && tar -xzf /tmp/helm.tgz -C /tmp \
    && mv /tmp/linux-amd64/helm /usr/local/bin/helm \
    && chmod 0755 /usr/local/bin/helm \
    && rm -rf /tmp/helm.tgz /tmp/linux-amd64 \
    && curl -fsSL "https://github.com/mikefarah/yq/releases/download/${YQ_VERSION}/yq_linux_amd64" \
      -o /usr/local/bin/yq \
    && chmod 0755 /usr/local/bin/yq \
    && if [ "${INSTALL_AWSCLI}" = "true" ]; then \
         curl -fsSL "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o /tmp/awscliv2.zip \
         && unzip -q /tmp/awscliv2.zip -d /tmp \
         && /tmp/aws/install \
         && rm -rf /tmp/aws /tmp/awscliv2.zip; \
       fi \
    && rm -rf /var/lib/apt/lists/*

COPY agents/base/register-agent.sh /usr/local/bin/register-agent.sh
RUN chmod 0755 /usr/local/bin/register-agent.sh

USER jenkins
ENTRYPOINT ["/usr/local/bin/register-agent.sh"]
