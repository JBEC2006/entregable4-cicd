pipeline {
    agent any

    options {
        disableConcurrentBuilds()
    }

    triggers {
        pollSCM('* * * * *')
    }

    parameters {
        string(name: 'RUTA_HUMO', defaultValue: '/', description: 'Ruta que se usa en la prueba de humo')
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Descargando el código del repositorio...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compilando la aplicación...'
                bat 'mvn -B clean compile'
            }
        }

        stage('Tests unitarios') {
            steps {
                echo 'Ejecutando los tests unitarios...'
                bat 'mvn -B test'
            }
        }

        stage('Tests de integración') {
            steps {
                echo 'Ejecutando los tests de integración...'
                bat 'mvn -B verify -DskipUnitarios=true'
            }
        }

        stage('Empaquetado') {
            steps {
                echo 'Guardando el jar en el repositorio de artefactos...'
                archiveArtifacts artifacts: 'web/target/catalogo.jar', fingerprint: true
            }
        }

        stage('Deploy a producción') {
            steps {
                echo 'Desplegando el jar en producción...'
                bat 'call scripts\\deploy.bat web\\target\\catalogo.jar'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
        }
        success {
            echo 'Pipeline terminado bien, la nueva versión quedó en producción'
        }
        failure {
            echo 'El pipeline falló, producción sigue con la versión anterior'
        }
        cleanup {
            cleanWs()
        }
    }
}
