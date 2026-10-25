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

        stage('SonarQube Analysis') {
            steps {
                // Placeholder for Task 5 (SonarQube/Trivy). 
                // For now, we just echo to show the stage exists.
                // echo 'Security checks will be implemented in Task 5'
                        withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh """
                        mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                        -Dsonar.projectKey=finsecure-api \
                        -Dsonar.host.url=http://host.docker.internal:9000 \
                        -Dsonar.login=\${SONAR_TOKEN}
                    """
                }
            }
        }

        // stage('OWASP Dependency Check') {
        //     steps {
        //         withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')]) {
        //             sh 'echo "Key length: ${#NVD_API_KEY}"'
        //             sh "mvn org.owasp:dependency-check-maven:check -DnvdApiKey=\${NVD_API_KEY} -DnvdApiDelay=8000"
        //         }
        //     }
        // }
        
        stage('Container Build') {
            steps {
                sh "docker build -t ${DOCKER_HUB_REPO}:${IMAGE_TAG} ."
                sh "docker tag ${DOCKER_HUB_REPO}:${IMAGE_TAG} ${DOCKER_HUB_REPO}:latest"
            }
        }

        stage('Trivy Security Scan') {
            steps {
                sh "trivy image --ignorefile .trivyignore --exit-code 1 --severity HIGH,CRITICAL zhouyidi/finsecure-transaction-api:${IMAGE_TAG}"
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

    post {
        always {
            // mail (
            //     to: 'zhouyidi395@gmail.com',
            //     subject: "Jenkins Build ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //     body: "Build Status: ${currentBuild.currentResult}"
            // )
            script {
                // 1. Get the current build log
                def logContent = currentBuild.rawBuild.getLog(1000).join('\n')
                
                // 2. Extract lines that start with [ERROR] or contain "FAILURE!"
                def errorLines = logContent.readLines().findAll { 
                    it.contains('[ERROR]') || it.contains('FAILURE!') || it.contains('Tests run:')
                }.join('\n')

                // 3. If no error lines were found, provide a fallback message
                if (!errorLines) {
                    errorLines = "No specific error lines found. Please check the console output."
                }

                // 4. Send the email with the extracted error lines
                mail (
                    to: 'zhouyidi395@gmail.com',
                    subject: "Jenkins Build ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                        Build Status: ${currentBuild.currentResult}
                        
                        Job: ${env.JOB_NAME} #${env.BUILD_NUMBER}
                                                
                        Full Console Output: ${env.BUILD_URL}console
                    """
                )
            }
        }
    }
}