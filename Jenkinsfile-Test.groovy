pipeline {
  agent { label 'Test'}
  environment{
    DOCKERHUB_CREDENTIALS = credentials('kentan404-dockerhub')
    }
  stages {
    stage ('Build') {
      steps {
      sh 'rm -rf ./cypress'
      sh '''
        npm install
        npm run build
        sudo npm install -g serve
        serve -s build &
        '''
      }
    }

    stage ('Test') {
      steps {
      sh ''' 
        npm install cypress
        npm install mocha
        npx cypress run --spec ./cypress/integration/test.spec.js
        '''
      }
    }

    stage ('Docker Login') {
      steps {
        sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
      }
    }

    stage ('Dockerize') {
      steps {
        sh 'docker build -t dec4 .'
      }
    }

    stage ('Push') {
      steps {
        sh '''
        docker push kentan404/deploy8:dec4
        '''
      }
    }


  }
}