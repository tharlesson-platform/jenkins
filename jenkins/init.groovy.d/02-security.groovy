import hudson.security.HudsonPrivateSecurityRealm
import jenkins.model.Jenkins

def jenkins = Jenkins.get()
def env = System.getenv()
def adminUser = env.getOrDefault("JENKINS_ADMIN_USER", "admin")
def adminPassword = env.getOrDefault("JENKINS_ADMIN_PASSWORD", "change_me_now")

def ensureLocalAccount = { HudsonPrivateSecurityRealm realm ->
  if (realm.getUser(adminUser) == null) {
    realm.createAccount(adminUser, adminPassword)
    println("--> Usuario admin local '${adminUser}' criado.")
  } else {
    println("--> Usuario admin local '${adminUser}' ja existe.")
  }
}

def securityRealm = jenkins.getSecurityRealm()
if (securityRealm instanceof HudsonPrivateSecurityRealm) {
  ensureLocalAccount((HudsonPrivateSecurityRealm) securityRealm)
} else {
  def internalRealm = null
  if (securityRealm.metaClass.respondsTo(securityRealm, "getInternalUsersDatabase")) {
    internalRealm = securityRealm.getInternalUsersDatabase()
  }

  if (internalRealm instanceof HudsonPrivateSecurityRealm) {
    ensureLocalAccount((HudsonPrivateSecurityRealm) internalRealm)
    println("--> Usuario local de contingencia tratado via base interna do Active Directory.")
  } else {
    println("--> Realm de seguranca nao expoe base interna de usuarios. Pulando criacao de usuario local.")
  }
}

jenkins.save()

