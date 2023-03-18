def datetime = new Date().format('yyyyMMddHHmmss')

pipeline {
    agent any

    tools {
        jdk 'openjdk17'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
    }

    stages {
        stage('Checkout') {
            steps {
                // git url: 'https://github.com/ioridazo/selenium.git', branch: 'develop'
                checkout scm
            }
        }

        stage('Test') {
            steps {
                bat "./mvnw clean"
                bat "./mvnw test surefire-report:report pmd:pmd pmd:cpd jacoco:report spotbugs:spotbugs"
            }
        }

        stage('Analytics') {
            steps {
                parallel(
                        junit: {
                            junit '**/target/surefire-reports/TEST-*.xml'
                        },
                        jacoco: {
                            jacoco(
                                    execPattern: 'target/*.exec',
                                    classPattern: 'target/classes',
                                    sourcePattern: 'src/main/java',
                                    exclusionPattern: 'src/test*'
                            )
                        },
                        pmd: {
                            recordIssues(tools: [pmdParser(pattern: 'target/pmd.xml')])
                        },
                        cpd: {
                            recordIssues(tools: [cpd(pattern: 'target/cpd.xml')])
                        },
                        spotBugs: {
                            recordIssues(tools: [spotBugs(pattern: 'target/spotbugsXml.xml', useRankAsPriority: true)])
                        },
                        clover: {
                            clover(
                                    cloverReportDir: 'target/site',
                                    cloverReportFileName: 'clover.xml',
                                    // optional, default is: method=70, conditional=80, statement=80
                                    healthyTarget: [methodCoverage: 70, conditionalCoverage: 80, statementCoverage: 80],
                                    // optional, default is none
                                    unhealthyTarget: [methodCoverage: 50, conditionalCoverage: 50, statementCoverage: 50],
                                    // optional, default is none
                                    failingTarget: [methodCoverage: 0, conditionalCoverage: 0, statementCoverage: 0]
                            )
                        }
                )
            }
        }

        stage('Build') {
            steps {
                bat "./mvnw package -DskipTests=true"
                archiveArtifacts artifacts: 'target/*.jar,target/*.zip', followSymlinks: false
            }
        }

        stage('Upload') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    nexusArtifactUploader(
                            nexusVersion: 'nexus3',
                            credentialsId: 'nexus3',
                            protocol: 'http',
                            nexusUrl: 'localhost:8081',
                            repository: 'maven-snapshots',
                            groupId: pom.groupId,
                            version: pom.version,
                            artifacts: [
                                    [
                                            artifactId: pom.artifactId,
                                            file      : 'target/selenium-' + pom.version + '.jar',
                                            type      : 'jar'
                                    ]
                            ]
                    )
                }
            }
        }

        stage('Download') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    env.URL = 'http://localhost:8081' +
                            '/service/rest/v1/search/assets/download' +
                            '?sort=version' +
                            '&repository=maven-releases' +
                            '&maven.groupId=' + pom.groupId +
                            '&maven.artifactId=' + pom.artifactId +
                            '&maven.baseVersion=' + pom.version
                    '&maven.extension=jar'
                    env.TMP = 'C:\\selenium\\bin\\tmp\\' + datetime + '\\'
                    env.JAR = 'selenium-' + pom.version + '.jar'
                    bat '''
                    mkdir %TMP%
                    bitsadmin /transfer "nexus" "%URL%" %TMP%%JAR%
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def jar = 'selenium-' + pom.version + '.jar'

                    bat '''
                    sc.exe stop selenium
                    @echo off
                    :DoWhile
                      sc.exe query selenium | findstr STATE | findstr STOPPED
                      if %errorlevel% equ 0 goto DoWhileExit
                    goto DoWhile
                    :DoWhileExit
                    '''
                    echo "*********************"
                    echo "サービスを停止しました。"
                    echo "*********************"
                    bat "sc.exe query selenium"

                    env.DATETIME = datetime
                    env.JAR = jar
                    bat '''
                    cd C:\\selenium\\bin
                    ren selenium.jar selenium.jar.%DATETIME%
                    copy C:\\selenium\\bin\\tmp\\%DATETIME%\\%JAR% selenium.jar
                    '''
                    echo "*********************"
                    echo "JARファイルを差し替えました。"
                    echo "*********************"

                    bat '''
                    sc.exe start selenium
                    @echo off
                    :DoWhile
                      sc.exe query selenium | findstr STATE | findstr RUNNING
                      if %errorlevel% equ 0 goto DoWhileExit
                    goto DoWhile
                    :DoWhileExit
                    '''
                    echo "*********************"
                    echo "サービスを起動しました。"
                    echo "*********************"
                    bat "sc.exe query selenium"
                }
            }
        }

        stage('Clean') {
            steps {
                script {
                    env.DATETIME = datetime
                    bat '''
                    cd C:\\selenium\\bin
                    del selenium.jar.%DATETIME%
                    '''
                }
            }
        }
    }

    post {
        success {
            slackSend(
                    baseUrl: 'https://pj-share-knowledges.slack.com/services/hooks/jenkins/',
                    channel: 'UKNK4SZPX',
                    color: 'good',
                    message: "${env.JOB_NAME} - ${env.BUILD_NUMBER} SUCCESS\n${env.BUILD_URL}",
                    notifyCommitters: true,
                    tokenCredentialId: 'Slack Jenkins Bot'
            )
        }

        failure {
            slackSend(
                    baseUrl: 'https://pj-share-knowledges.slack.com/services/hooks/jenkins/',
                    channel: 'UKNK4SZPX',
                    color: 'danger',
                    message: "${env.JOB_NAME} - ${env.BUILD_NUMBER} FAILED\n${env.BUILD_URL}",
                    notifyCommitters: true,
                    tokenCredentialId: 'Slack Jenkins Bot'
            )
        }
    }
}
