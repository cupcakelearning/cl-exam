pipeline {
    agent {
        label '!windows'
    }

    stages {
        stage('Build') {
            steps {
                sh "mvn clean package -Dmaven.test.skip=true"
            }
        }
        stage('Unit Test') {
            steps {
                sh "mvn test"
            }
        }
        stage('Push Image') {
            steps {
                sh "sudo sh ./scripts/pushing_ecr.sh"
            }
        }
        stage('Deploy') {
            steps {
                sh "sudo sh ./scripts/ci_script.sh"
            }
        }
    }
}