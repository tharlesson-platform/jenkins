import hudson.model.Node
import jenkins.model.Jenkins

def jenkins = Jenkins.get()
def env = System.getenv()
def executorCount = Integer.parseInt(env.getOrDefault("JENKINS_EXECUTORS", "0"))

jenkins.setSystemMessage(
  "Jenkins Controller inicializacao via codigo. " +
  "Compatibilidade de nomenclatura legada: master/workers = controller/agents."
)
jenkins.setNumExecutors(executorCount)
jenkins.setMode(Node.Mode.NORMAL)
jenkins.save()

println("--> 01-system.groovy aplicado. executors=${executorCount}")

