def isRelease = env.TAG_NAME

String version = "${env.BRANCH_NAME}${(isRelease ? ".${env.BUILD_NUMBER}" : '-SNAPSHOT')}"

node {
    try {
        stage('Checkout') {
            cleanWs()
            checkout scm
        }
        stage('Build') {
            echo "My branch is: ${env.BRANCH_NAME}"
            withSonarQubeEnv('localhostSonarQube') {
                bat "gradlew.bat --info clean build sonarqube -PinjectedVersion=${version}"
            }
            currentBuild.description = version
        }
        stage('Archive') {
            archiveArtifacts artifacts: 'build/**/*.jar'
            withCredentials([file(credentialsId: 'gpg-signed', variable: 'gradleProperties')]) {
                def fileContent = readFile gradleProperties
                writeFile file: 'gradle.properties', text: fileContent
                withCredentials([usernamePassword(credentialsId: 'nexus', passwordVariable: 'nexusPassword', usernameVariable: 'nexusUsername')]) {
                    bat "gradlew.bat --info upload -PinjectedVersion=${version} -PnexusUsername=${nexusUsername} -PnexusPassword=${nexusPassword}"
                }
                withCredentials([usernamePassword(credentialsId: 'oss.sonartype.org', passwordVariable: 'nexusPassword', usernameVariable: 'nexusUsername')]) {
                    def nexusUrlRelease = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                    def nexusUrlSnapshot = "https://oss.sonatype.org/content/repositories/snapshots"
                    bat "gradlew.bat --info upload -PinjectedVersion=${version} -PnexusUsername=${nexusUsername} -PnexusPassword=${nexusPassword} -PnexusUrlRelease=${nexusUrlRelease} -PnexusUrlSnapshot=${nexusUrlSnapshot}"
                }
            }
        }
    } catch (Throwable t) {
        throw t
    }
}
