pipeline {
  agent {
    docker {
      image 'maven:3.5.2-jdk-8'
    }
  }
  environment {
    stagingPostgresDbUrl = credentials('STAGINGMETADATAWSDBURL')
    fallBackPostgresDbUrl = credentials('STAGINGMETADATAWSDBURL')
    productionPostgresDbUrl = credentials('STAGINGMETADATAWSDBURL')
    postgresDBUserName = credentials('POSTGRESDBUSERNAME')
    postgresDBPassword = credentials('POSTGRESDBPASSWORD')
    tomcatCredentials = credentials('TOMCATCREDENTIALS')
    stagingHost = credentials('STAGINGHOST')
    fallbackHost = credentials('FALLBACKHOST')
    productionHost = credentials('PRODUCTIONHOST')
  }
  parameters {
    booleanParam(name: 'DeployToStaging' , defaultValue: false , description: '')
    booleanParam(name: 'DeployToProduction' , defaultValue: false , description: '')
  }
  stages {
    stage('Default Build pointing to Staging DB') {
      steps {
        sh "mvn clean package -DskipTests -DbuildDirectory=staging/target -Dmetadata-dbUrl=${stagingPostgresDbUrl} -Dmetadata-dbUsername=${postgresDBUserName} -Dmetadata-dbPassword=${postgresDBPassword}"
      }
    }
    stage('Build For FallBack And Production') {
      when {
        expression {
          params.DeployToProduction == true
        }
      }
      steps {
        echo 'Build pointing to FallBack DB'
        sh "mvn clean package -DskipTests -DbuildDirectory=fallback/target -Dmetadata-dbUrl=${fallBackPostgresDbUrl} -Dmetadata-dbUsername=${postgresDBUserName} -Dmetadata-dbPassword=${postgresDBPassword}"
        echo 'Build pointing to Production DB'
        sh "mvn clean package -DskipTests -DbuildDirectory=production/target -Dmetadata-dbUrl=${productionPostgresDbUrl} -Dmetadata-dbUsername=${postgresDBUserName} -Dmetadata-dbPassword=${postgresDBPassword}"
      }
    }
    stage('Deploy To Staging') {
      when {
        expression {
          params.DeployToStaging == true
        }
      }
      steps {
        echo 'Deploying to Staging'
        sh "curl --upload-file staging/target/metadata-ws-*.war 'http://'${tomcatCredentials}'@'${stagingHost}':8080/manager/text/deploy?path=/ega/ampt2d/metadata&update=true' | grep 'OK - Deployed application at context path '"
      }
    }
    stage('Deploy To FallBack And Production') {
      when {
        expression {
          params.DeployToProduction == true
        }
      }
      steps {
        echo 'Deploying to Fallback'
        sh "curl --upload-file fallback/target/metadata-ws-*.war 'http://'${tomcatCredentials}'@'${fallbackHost}':8080/manager/text/deploy?path=/ega/ampt2d/metadata&update=true' | grep 'OK - Deployed application at context path '"
        echo 'Deploying to Production'
        sh "curl --upload-file production/target/metadata-ws-*.war 'http://'${tomcatCredentials}'@'${productionHost}':8080/manager/text/deploy?path=/ega/t2d/metadata&update=true' | grep 'OK - Deployed application at context path '"
        archiveArtifacts artifacts: 'production/target/metadata-ws-*.war' , fingerprint: true
      }
    }
  }
}
