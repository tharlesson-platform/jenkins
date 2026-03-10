folder('CI') {
  description('Pipelines de Integracao Continua')
}
folder('CI/Java') {
  description('Pipelines CI para Java')
}
folder('CI/Python') {
  description('Pipelines CI para Python')
}
folder('CI/Node') {
  description('Pipelines CI para Node.js')
}
folder('CI/Go') {
  description('Pipelines CI para Go')
}
folder('CI/Containers') {
  description('Pipelines CI para containers')
}
folder('CD') {
  description('Pipelines de implantacao')
}
folder('CD/Apps') {
  description('Pipelines de implantacao por ambiente')
}
folder('Admin') {
  description('Jobs administrativos')
}

def runtimeEnv = System.getenv()
def defaultRepo = runtimeEnv.getOrDefault('SEED_DEFAULT_REPO_URL', 'https://github.com/example/jenkins-platform.git')
def defaultBranch = runtimeEnv.getOrDefault('SEED_DEFAULT_BRANCH', 'main')

def createPipelineFromRepo = { String jobPath, String scriptPath, String descriptionText ->
  pipelineJob(jobPath) {
    description(descriptionText)
    parameters {
      stringParam('PIPELINE_REPO_URL', defaultRepo, 'URL do repositorio que contem os Jenkinsfiles.')
      stringParam('PIPELINE_REPO_BRANCH', defaultBranch, 'Branch do repositorio para carregar o Jenkinsfile.')
    }
    definition {
      cpsScm {
        scm {
          git {
            remote {
              url('${PIPELINE_REPO_URL}')
            }
            branch('${PIPELINE_REPO_BRANCH}')
          }
        }
        scriptPath(scriptPath)
        lightweight(true)
      }
    }
  }
}

def createMultibranchFromRepo = { String jobPath, String scriptPath, String descriptionText ->
  multibranchPipelineJob(jobPath) {
    description(descriptionText)
    branchSources {
      branchSource {
        source {
          git {
            id("${jobPath}-git-source".replaceAll('[^a-zA-Z0-9-]', '-'))
            remote(defaultRepo)
            credentialsId('')
            includes('*')
            excludes('')
          }
        }
      }
    }
    factory {
      workflowBranchProjectFactory {
        scriptPath(scriptPath)
      }
    }
    orphanedItemStrategy {
      discardOldItems {
        numToKeep(20)
      }
    }
  }
}

pipelineJob('Admin/Platform-Smoke') {
  description('Teste de fumaca inicial da plataforma Jenkins.')
  definition {
    cps {
      script('''pipeline {
        agent any
        options { timestamps() }
        stages {
          stage('Jenkins') {
            steps {
              sh 'curl -fsS http://localhost:8080/login >/dev/null'
            }
          }
          stage('Informacoes do Controller') {
            steps {
              sh 'java -version'
            }
          }
        }
      }'''.stripIndent())
      sandbox()
    }
  }
}

createPipelineFromRepo(
  'CI/Java/java-maven-app-ci',
  'pipelines/ci/Jenkinsfile.java.ci',
  'Pipeline CI Java com testes, qualidade, seguranca, build de container e publicacao no Nexus.'
)

createMultibranchFromRepo(
  'CI/Java/java-maven-app-mb',
  'pipelines/ci/Jenkinsfile.java.ci',
  'Pipeline CI Java multibranch (script vindo do monorepo).'
)

createPipelineFromRepo(
  'CI/Python/python-app-ci',
  'pipelines/ci/Jenkinsfile.python.ci',
  'Pipeline CI Python com lint, testes, cobertura, SAST e build opcional de container.'
)

createMultibranchFromRepo(
  'CI/Python/python-app-mb',
  'pipelines/ci/Jenkinsfile.python.ci',
  'Pipeline CI Python multibranch.'
)

createPipelineFromRepo(
  'CI/Node/node-app-ci',
  'pipelines/ci/Jenkinsfile.node.ci',
  'Pipeline CI Node com npm ci, lint/testes/cobertura, sonar e build opcional de imagem.'
)

createMultibranchFromRepo(
  'CI/Node/node-app-mb',
  'pipelines/ci/Jenkinsfile.node.ci',
  'Pipeline CI Node multibranch.'
)

createPipelineFromRepo(
  'CI/Go/go-app-ci',
  'pipelines/ci/Jenkinsfile.go.ci',
  'Pipeline CI Go com fmt/vet/test/lint/build e publicacao opcional de imagem.'
)

createMultibranchFromRepo(
  'CI/Go/go-app-mb',
  'pipelines/ci/Jenkinsfile.go.ci',
  'Pipeline CI Go multibranch.'
)

createPipelineFromRepo(
  'CI/Containers/container-image-ci',
  'pipelines/ci/Jenkinsfile.container.ci',
  'Pipeline CI generico de container com buildx, trivy e SBOM/push opcional.'
)

createMultibranchFromRepo(
  'CI/Containers/container-image-mb',
  'pipelines/ci/Jenkinsfile.container.ci',
  'Pipeline CI generico de container multibranch.'
)

createPipelineFromRepo(
  'CD/Apps/docker-cd',
  'pipelines/cd/Jenkinsfile.docker.cd',
  'Pipeline CD para implantacao em host Docker/VM com aprovacao e reversao.'
)

createPipelineFromRepo(
  'CD/Apps/k8s-cd',
  'pipelines/cd/Jenkinsfile.k8s.cd',
  'Pipeline CD para implantacao em Kubernetes com verificacao de rollout e reversao.'
)

createPipelineFromRepo(
  'CD/Apps/ecs-cd',
  'pipelines/cd/Jenkinsfile.ecs.cd',
  'Pipeline CD para atualizacao de servico ECS com promocao/reversao de task definition.'
)

createPipelineFromRepo(
  'CD/Apps/vm-cd',
  'pipelines/cd/Jenkinsfile.vm.cd',
  'Pipeline CD para implantacao em VM por versao com reversao por symlink.'
)


