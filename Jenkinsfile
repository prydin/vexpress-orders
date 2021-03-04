pipeline {
    agent any

    parameters {
        string(defaultValue: '', description: 'The RabbitMQ broker address', name: 'RABBITMQ_IP', trim: true)
        string(defaultValue: 'dev', description: 'Target environment', name: 'ENVIRONMENT', trim: true)
    }
}

stages {
    stage('Init') {
        steps {
            script {
                def gradle = readFile(file: 'build.gradle')
                env.version = (gradle =~ /version\s*=\s*["'](.+)["']/)[0][1]
                echo "Inferred version: ${env.version}"
                env.RABBITMQ_IP = params.RABBITMQ_IP
                if (!env.RABBITMQ_IP) {
                    env.RABBITMQ_IP = getDefaultRabbitMqIp()
                }
                print "${env} ${params}"
                print "${env.RABBITMQ_IP} ${params.RABBITMQ_IP}"
                env.ENVIRONMENT = params.ENVIRONMENT
            }
        }
    }

    stage('Build') {
        steps {
            sh './gradlew clean assemble'
        }
    }

    stage('Test') {
        steps {
            sh './gradlew test'
            junit 'build/test-results/test/*.xml'
        }
    }

    stage('Publish') {
        steps {
            archiveArtifacts(artifacts: "build/libs/vexpress-orders-${env.version}.jar", fingerprint: true, onlyIfSuccessful: true)
        }
    }

    stage("InitDeployment") {
        steps {
            withCredentials([usernamePassword(credentialsId: 'apiToken', passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
                script {
                    env.apiUser = USER
                    env.apiToken = PASSWORD
                }
            }
        }
    }

    stage('DeployVMs') {
        steps {

            withCredentials([usernamePassword(credentialsId: 'sshCreds', passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
                script {
                    def depId = vraDeployFromCatalog(
                            configFormat: "yaml",
                            config: readFile('infra/appserver.yaml'))[0].id
                    vraWaitForAddress(
                            deploymentId: depId,
                            resourceName: 'JavaServer')[0]
                    env.appIp = getInternalAddress(depId, "JavaServer")
                    echo "Deployed: ${depId} address: ${env.appIp}"
                }
            }
        }
    }

    stage('Configure') {
        steps {
            withCredentials([usernamePassword(credentialsId: 'sshCreds', passwordVariable: 'PASSWORD', usernameVariable: 'USER'),
                             usernamePassword(credentialsId: 'rabbitMqCreds', passwordVariable: 'RABBITMQ_PASSWORD', usernameVariable: 'RABBITMQ_USER')]) {
                script {
                    def txt = readFile(file: 'templates/application-properties.tpl')
                    txt = txt.replace('$RABBITMQ_IP', env.RABBITMQ_IP).
                            replace('$RABBITMQ_USER', env.RABBITMQ_USER).
                            replace('$RABBITMQ_PASSWORD', env.RABBITMQ_PASSWORD)
                    writeFile(file: "application.properties", text: txt)

                    def remote = [:]
                    remote.name = 'appServer'
                    remote.host = env.appIp
                    remote.user = USER
                    remote.password = PASSWORD
                    remote.allowAnyHosts = true

                    // The first first attempt may fail if cloud-init hasn't created user account yet
                    retry(20) {
                        sleep time: 10, unit: 'SECONDS'
                        sshPut remote: remote, from: 'application.properties', into: '/tmp'
                    }
                    sshPut remote: remote, from: 'scripts/vexpress-orders.service', into: '/tmp'
                    sshPut remote: remote, from: 'scripts/configureAppserver.sh', into: '/tmp'
                    sshCommand remote: remote, command: 'chmod +x /tmp/configureAppserver.sh'
                    sshCommand remote: remote, sudo: true, command: "/tmp/configureAppserver.sh ${USER} ${env.apiUser} ${env.apiToken} ${env.BUILD_URL} ${env.version}"
                }
            }
        }
    }
    stage("Finalize") {
        steps {
            // Store build state
            withAWS(credentials: 'jenkins') {
                writeJSON(file: 'state.json', json: ['url': "http://${env.appIp}:8080"])
                s3Upload(file: 'state.json', bucket: 'prydin-build-states', path: "vexpress/orders/${env.ENVIRONMENT}/state.json")
            }
        }
    }
}
}

def getInternalAddress(id, resourceName) {
    def dep = vraGetDeployment(
            deploymentId: id,
            expandResources: true
    )
    return dep.resources.find({ it.name == resourceName }).properties.networks[0].address
}

def getDefaultRabbitMqIp() {
    // Store build state
    withAWS(credentials: 'jenkins') {
        s3Download(file: 'state.json', bucket: 'prydin-build-states', path: "vexpress/scheduling/${env.ENVIRONMENT}/state.json", force: true)
        def json = readJSON(file: 'state.json')
        print("Found deployment record: " + json)
        return json.rabbitMqIp
    }
}

