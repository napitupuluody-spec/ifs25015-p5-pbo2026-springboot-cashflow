pipeline {
    agent any

    environment {
        APP_NAME = 'ifs25015-p5-pbo2026-springboot-cashflow'
        // Isi REGISTRY (mis. 'ghcr.io/namauser' atau 'registry.delcom.org/pbo2026') kalau punya registry asli.
        // Dikosongkan = image hanya dibuat lokal di server Jenkins (tanpa push).
        REGISTRY = ''
        IMAGE    = "${APP_NAME}"
        TAG      = "${env.BUILD_NUMBER}"
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Build & Test') {
            steps {
                sh './mvnw -Dmaven.repo.local=.m2/repository -B clean test'
            }
            post {
                always { junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml' }
            }
        }

        stage('Jacoco 100%') {
            steps {
                // gagal bila coverage counter terukur < 100% (view & auth dikecualikan di pom.xml)
                sh './mvnw -Dmaven.repo.local=.m2/repository -B verify'
            }
            post {
                always { archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true }
            }
        }

        stage('Trivy Scan') {
            steps {
                // Agent Jenkins tidak punya trivy terpasang -> pakai yang ada di PATH,
                // jika tidak ada, unduh binary ke workspace (tanpa perlu root).
                sh '''
                    set -e
                    TRIVY_BIN_DIR="$WORKSPACE/.trivy-bin"
                    if command -v trivy >/dev/null 2>&1; then
                        TRIVY=trivy
                    else
                        if [ ! -x "$TRIVY_BIN_DIR/trivy" ]; then
                            mkdir -p "$TRIVY_BIN_DIR"
                            curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh \\
                                | sh -s -- -b "$TRIVY_BIN_DIR"
                        fi
                        TRIVY="$TRIVY_BIN_DIR/trivy"
                    fi
                    "$TRIVY" --version
                    "$TRIVY" fs --cache-dir "$WORKSPACE/.trivy-cache" \\
                        --scanners vuln --exit-code 1 --severity HIGH,CRITICAL \\
                        --skip-dirs target,node_modules,.m2,.trivy-bin,.trivy-cache,.git .
                '''
            }
        }

        stage('Package') {
            steps {
                sh './mvnw -Dmaven.repo.local=.m2/repository -B clean package -Pproduction -DskipTests'
            }
        }

        stage('Publish') {
            steps {
                sh '''
                    docker build -t ${IMAGE}:${TAG} -t ${IMAGE}:latest .
                    if [ -n "${REGISTRY}" ]; then
                        docker tag ${IMAGE}:${TAG} ${REGISTRY}/${IMAGE}:${TAG}
                        docker tag ${IMAGE}:latest ${REGISTRY}/${IMAGE}:latest
                        docker push ${REGISTRY}/${IMAGE}:${TAG}
                        docker push ${REGISTRY}/${IMAGE}:latest
                    else
                        echo "REGISTRY kosong -> lewati docker push (image hanya lokal)"
                    fi
                '''
            }
        }

        stage('Deploy') {
            steps {
                // Deploy ke domain s1if.cloud dilakukan oleh Delcom (clone dari GitHub lalu build Dockerfile).
                // Stage ini menandai bahwa seluruh tahap CI (test, coverage, scan, package, image) lulus.
                echo "CI lulus untuk ${APP_NAME} build #${env.BUILD_NUMBER}. Deploy ke Delcom: Aksi -> Redeploy."
            }
        }
    }

    post {
        failure { echo 'Pipeline gagal - periksa stage yang merah.' }
    }
}
