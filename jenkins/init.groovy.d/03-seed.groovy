import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def jenkins = Jenkins.get()
def jobName = "seed-local-files"

def pipelineScript = """
pipeline {
  agent any
  options { timestamps() }
  stages {
    stage('Preparar Seed DSL') {
      steps {
        sh '''
          set -e
          rm -rf .seed
          mkdir -p .seed
          cp -r /var/jenkins_home/seed-jobs/jobdsl .seed/
        '''
      }
    }
    stage('Executar Job DSL') {
      steps {
        jobDsl targets: '.seed/jobdsl/*.groovy',
               removedJobAction: 'DISABLE',
               removedViewAction: 'DELETE',
               failOnMissingPlugin: true
      }
    }
  }
}
""".stripIndent()

WorkflowJob seedJob = (WorkflowJob) jenkins.getItem(jobName)
if (seedJob == null) {
  seedJob = jenkins.createProject(WorkflowJob, jobName)
}

seedJob.setDefinition(new CpsFlowDefinition(pipelineScript, true))
seedJob.setDescription("Seed job que provisiona pastas/jobs a partir de /var/jenkins_home/seed-jobs/jobdsl")
seedJob.save()

println("--> 03-seed.groovy aplicado. Job '${jobName}' pronto.")
