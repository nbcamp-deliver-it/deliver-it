pipeline {
  agent any

  environment {
    JAVA_HOME = '/usr/lib/jvm/java-17-amazon-corretto'
    REPO_URL   = 'https://github.com/nbcamp-deliver-it/deliver-it.git'
    BRANCH     = 'test-deploy'
    SSH_USER   = 'ec2-user'               // 원격 접속 계정
    APP_JAR    = '/opt/app/app.jar'       // 배포 타깃 경로
    SERVICE    = 'app'                    // systemd 서비스명
    HEALTH_PORT = '8080'
    HEALTH_PATH = '/actuator/health'
    GRADLE_OPTS = "-Dorg.gradle.java.installations.paths=/usr/lib/jvm/java-17-amazon-corretto -Dorg.gradle.java.installations.auto-detect=true"

    // Jenkins Credentials IDs
    GIT_CRED_ID = 'git-cred'
    SSH_CRED_ID = 'ec2-ssh-key'
  }

  options {
    timestamps()
    ansiColor('xterm')
  }

  stages {
    stage('Checkout') {
      steps {
        checkout([$class: 'GitSCM',
          branches: [[name: "*/${BRANCH}"]],
          userRemoteConfigs: [[url: "${REPO_URL}", credentialsId: "${GIT_CRED_ID}"]]
        ])
      }
    }

    stage('Resolve HOSTS from Secret') {
      steps {
        withCredentials([string(credentialsId: 'deliverit-hosts', variable: 'HOSTS_SECRET')]) {
          script {
            env.HOSTS = HOSTS_SECRET.trim()
            echo "Loaded HOSTS (count=${env.HOSTS.split(/\\s+/).size()})"
          }
        }
      }
    }

		stage('Check Java') {
      steps {
        sh '''
          echo "JAVA_HOME=$JAVA_HOME"
          which java || true
          java -version || true
          which javac || true
          javac -version || true
        '''
      }
    }

    stage('Build (Gradle)') {
      steps {
        sh '''
					export JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto
					export PATH=$JAVA_HOME/bin:$PATH
					chmod +x ./gradlew
					./gradlew \
						-Dorg.gradle.java.installations.paths=/usr/lib/jvm/java-17-amazon-corretto \
						-Dorg.gradle.java.installations.auto-detect=true \
						clean bootJar -x test --no-daemon
        '''
      }
    }

    stage('Pick Artifact') {
      steps {
        script {
          def jar = sh(script: "ls -1 build/libs/*.jar | head -n 1", returnStdout: true).trim()
          if (!jar) { error "Artifact(.jar) not found in build/libs" }
          env.LOCAL_JAR = jar
          echo "Artifact: ${env.LOCAL_JAR}"
        }
      }
    }

    stage('Rolling Deploy') {
      steps {
        sshagent (credentials: [env.SSH_CRED_ID]) {
          script {
            def hosts = env.HOSTS.split("\\s+")
            for (h in hosts) {
              echo "=== Deploy to ${h} ==="
              sh """
                set -eux

                # 1) 업로드 (첫 연결 known_hosts 피하려고 체크 끔)
                scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null \
                    "\${LOCAL_JAR}" ${SSH_USER}@${h}:/tmp/app.jar

                # 2) 교체 및 재기동
                ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${SSH_USER}@${h} '
                  set -eux
                  sudo systemctl stop ${SERVICE} || true
                  sudo mv /tmp/app.jar ${APP_JAR}
                  sudo chown appuser:appuser ${APP_JAR} || true
                  sudo systemctl start ${SERVICE}
                '

                # 3) 헬스체크 (해당 호스트 자기 자신 :8080)
                for i in \$(seq 1 30); do
                  status=\$(curl -s http://${h}:${HEALTH_PORT}${HEALTH_PATH} || true)
                  echo "Health: \$status"
                  echo "\$status" | grep -q '"status":"UP"' && ok=1 || ok=0
                  [ \$ok -eq 1 ] && break
                  sleep 2
                done
                [ \${ok:-0} -eq 1 ] || { echo "Health check failed on ${h}"; exit 1; }

                echo ">>> ${h} OK"
              """
            }
          }
        }
      }
    }
  }

  post {
    success {
      echo "✅ Rolling deploy completed."
    }
    failure {
      echo "❌ Deploy failed."
    }
  }
}

