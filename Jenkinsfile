pipeline {
    agent any

    environment {
        DOCKER_HUB_REPO = "zhouyidi/finsecure-transaction-api"
        IMAGE_TAG = "v${BUILD_NUMBER}" // Uses Jenkins build number for versioning
        DOCKER_CREDS = credentials('dockerhub-creds')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Year4Sem2/finsecure_pay.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                // This will fail the pipeline if tests fail (aborting deployment)
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Security Checks') {
            steps {
                // Placeholder for Task 5 (SonarQube/Trivy). 
                // For now, we just echo to show the stage exists.
                echo 'Security checks will be implemented in Task 5'
            }
        }

        stage('Container Build') {
            steps {
                sh "docker build -t ${DOCKER_HUB_REPO}:${IMAGE_TAG} ."
                sh "docker tag ${DOCKER_HUB_REPO}:${IMAGE_TAG} ${DOCKER_HUB_REPO}:latest"
            }
        }

        stage('Push') {
            steps {
                sh "echo ${DOCKER_CREDS_PSW} | docker login -u ${DOCKER_CREDS_USR} --password-stdin"
                sh "docker push ${DOCKER_HUB_REPO}:${IMAGE_TAG}"
                sh "docker push ${DOCKER_HUB_REPO}:latest"
            }
        }

        stage('Deploy') {
            steps {
                // Use the kubeconfig credential to deploy to EKS
                withCredentials([
    file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG'),
    usernamePassword(credentialsId: 'aws-creds', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')
                ]) {
                    // Update the deployment image to the new tag
                    sh "kubectl set image deployment/finsecure-api finsecure-api=${DOCKER_HUB_REPO}:${IMAGE_TAG} --record"
                    // Wait for the rollout to finish
                    sh "kubectl rollout status deployment/finsecure-api"
                }
            }
        }
    }
}