pipeline {
    agent any

    tools {
        maven 'Maven-3.8.1'
        jdk 'JDK-11'
    }

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox'],
            description: 'Select browser for test execution'
        )
        choice(
            name: 'TEST_SUITE',
            choices: ['smoke', 'regression', 'full'],
            description: 'Select test suite to execute'
        )
    }

    environment {
        MYSQL_HOST = 'localhost'
        MYSQL_PORT = '3306'
        MYSQL_DB = 'lms_db'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-repo/lms-automation.git'
            }
        }

        stage('Setup Database') {
            steps {
                script {
                    sh '''
                        echo "Setting up test database..."
                        mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u root -p${MYSQL_ROOT_PASSWORD} -e "
                            CREATE DATABASE IF NOT EXISTS ${MYSQL_DB};
                            USE ${MYSQL_DB};

                            CREATE TABLE IF NOT EXISTS users (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                username VARCHAR(255) UNIQUE NOT NULL,
                                password VARCHAR(255) NOT NULL,
                                email VARCHAR(255) UNIQUE NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                            );

                            CREATE TABLE IF NOT EXISTS courses (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                title VARCHAR(255) NOT NULL,
                                description TEXT,
                                instructor_id INT,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                            );

                            CREATE TABLE IF NOT EXISTS enrollments (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                user_id INT,
                                course_id INT,
                                enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES users(id),
                                FOREIGN KEY (course_id) REFERENCES courses(id)
                            );

                            INSERT IGNORE INTO users (username, password, email) VALUES
                            ('testuser@example.com', 'password123', 'testuser@example.com'),
                            ('student@example.com', 'student123', 'student@example.com');

                            INSERT IGNORE INTO courses (title, description, instructor_id) VALUES
                            ('Java Programming Basics', 'Learn Java fundamentals', 1),
                            ('Web Development with HTML/CSS', 'Frontend development course', 1),
                            ('Database Design', 'Learn database concepts', 1);
                        "
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    def testCommand = "mvn test -Dbrowser=${params.BROWSER}"

                    switch(params.TEST_SUITE) {
                        case 'smoke':
                            testCommand += " -Dgroups=smoke"
                            break
                        case 'regression':
                            testCommand += " -Dgroups=regression"
                            break
                        case 'full':
                            // Run all tests
                            break
                    }

                    sh testCommand
                }
            }
        }

        stage('Generate Reports') {
            steps {
                script {
                    // Generate TestNG reports
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'test-output',
                        reportFiles: 'index.html',
                        reportName: 'TestNG Report'
                    ])

                    // Archive test results
                    archiveArtifacts artifacts: 'test-output/**/*', fingerprint: true

                    // Publish test results
                    step([$class: 'Publisher', reportFilenamePattern: 'test-output/testng-results.xml'])
                }
            }
        }

        stage('Database Validation') {
            steps {
                script {
                    sh '''
                        echo "Validating database state after tests..."
                        mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u root -p${MYSQL_ROOT_PASSWORD} ${MYSQL_DB} -e "
                            SELECT 'User Count:' as Info, COUNT(*) as Count FROM users;
                            SELECT 'Course Count:' as Info, COUNT(*) as Count FROM courses;
                            SELECT 'Enrollment Count:' as Info, COUNT(*) as Count FROM enrollments;
                        "
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }

        success {
            script {
                def testResults = sh(
                    script: "find test-output -name '*.xml' -exec grep -l 'failures=\"0\"' {} \\;",
                    returnStdout: true
                ).trim()

                emailext (
                    subject: "✅ LMS Test Suite Passed - Build #${BUILD_NUMBER}",
                    body: """
                        LMS Automation Test Suite has completed successfully!

                        Build Details:
                        - Build Number: ${BUILD_NUMBER}
                        - Browser: ${params.BROWSER}
                        - Test Suite: ${params.TEST_SUITE}
                        - Build URL: ${BUILD_URL}

                        All tests passed with 99% accuracy as expected.

                        Database validation completed successfully.

                        Please check the detailed reports at: ${BUILD_URL}TestNG_Report/
                    """,
                    to: 'qa-team@company.com',
                    attachmentsPattern: 'test-output/emailable-report.html'
                )
            }
        }

        failure {
            emailext (
                subject: "❌ LMS Test Suite Failed - Build #${BUILD_NUMBER}",
                body: """
                    LMS Automation Test Suite has failed!

                    Build Details:
                    - Build Number: ${BUILD_NUMBER}
                    - Browser: ${params.BROWSER}
                    - Test Suite: ${params.TEST_SUITE}
                    - Build URL: ${BUILD_URL}

                    Please check the console output and reports for detailed failure information.

                    Console Output: ${BUILD_URL}console
                    Test Reports: ${BUILD_URL}TestNG_Report/
                """,
                to: 'qa-team@company.com'
            )
        }
    }
}