def repositoryUrl
def PACKAGE_NAME
def PACKAGE_VERSION
def RESTARTSTAGE=false
def mainbuildnumber
def mainpackagename
def prev_build_number

properties([
  parameters([
       booleanParam(name: 'EnableFortifyScan', defaultValue: false, description: "Enable for fortify scan results, this will generate PDF copy in Workspace and send you an email attached with the report.")
  ])
])


pipeline {

  agent none

  options {
    skipDefaultCheckout true
  }                
 
    environment {
        ProjectName = 'npx2-paynow'
        mainjar = 'npx2-paynow'
        L2Email = "${env.L2_Approvers_OpenSystem}"
        tagname = "${ProjectName}_${env.BRANCH_NAME}_${env.BUILD_NUMBER}"
        filename = "${ProjectName}_${env.BUILD_NUMBER}"
        prod_sftp_absolute_path = "${env.prod_sftp_ab_path}"
        artifactrepo = "NPX2_SIT/nets"
        sonarAnalyze = false
    }
    
    tools {
        gradle "Gradle6.0.1"
    }
  
    stages {
    stage('Checkout SCM') {
    agent { label 'master' }
        steps {
            disableRestart ("${STAGE_NAME}")
            step([$class: 'WsCleanup'])
            checkout scm            
            }
    }
        
    stage('Build') {
    agent { label 'master' }
        steps {
            disableRestart ("${STAGE_NAME}")
            sh 'gradle clean bootJar'
            sh 'gradle copyArtifacts'
        }
        
        post {  

             failure { 
                getDevRecipients()
                emailext (
                subject: "ERROR: Build failure notification for project ${env.JOB_NAME} branch",
                to: "${tolist}",
                body: "Following build failed due to compilation error. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL}"
                )
             }  
             fixed {  
                getDevRecipients()
                emailext (
                subject: "Resolved: Build success notification for project: ${env.JOB_NAME} branch",
                to: "${tolist}",
                body: "Project build successfully executed after previous build failure. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL}"
                )
             }  
        }  
    } 
        
    stage ('Nexus Lifecycle Analysis') {
    agent { label 'master' }
        steps{
            disableRestart ("${STAGE_NAME}")
            echo 'Calling lifecyle for opensource library scanning'
            script {
            repositoryUrl = sh(returnStdout: true, script: 'git config remote.origin.url').trim()
            echo 'Repo URL '+repositoryUrl
            if(!repositoryUrl.contains(env.GIT_REPO_VLAN13))
                {
                    echo 'Git Corp/Mirror repo being used'
                    //nexusPolicyEvaluation failBuildOnNetworkError: true, iqApplication: selectedApplication("$ProjectName"), iqStage: 'build', jobCredentialsId: ''
                    echo 'Calling lifecyle for opensource library scanning'
                    def policyEvaluation = nexusPolicyEvaluation failBuildOnNetworkError: true, iqApplication: selectedApplication("$ProjectName"), iqStage: 'build', jobCredentialsId: ''
                    def iqurl = policyEvaluation.applicationCompositionReportUrl.substring(0, policyEvaluation.applicationCompositionReportUrl.indexOf(":8070/")+5)
                    def substringreportid = policyEvaluation.applicationCompositionReportUrl.substring(policyEvaluation.applicationCompositionReportUrl.lastIndexOf("/")+1,policyEvaluation.applicationCompositionReportUrl.length())
                    echo 'substringreportid: '+substringreportid
                    withCredentials([usernamePassword(credentialsId: 'jenkins-nexusiqserver', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh "curl ${iqurl}/rest/report/${ProjectName}/${substringreportid}/printReport -o NSR_${ProjectName}.pdf -v -u $USERNAME:$PASSWORD"
                        }
                        emailext attachmentsPattern: "**/NSR_${ProjectName}.pdf", body: 'Attached reports on nexus package scan', subject: "$ProjectName Nexus Scan report", to: "${tolist}"

                }
            else 
                {
                    echo 'VLAN13 repo being used'
                }
            }
            
            
        }
    }

    stage('Static Code Analysis') {
    steps{
        disableRestart ("${STAGE_NAME}")
        node('StaticCodeAnalysis'){
        step([$class: 'WsCleanup'])
            script {
            
            def isFortifyScan = env.EnableFortifyScan
            echo "Fortify SCA is $isFortifyScan!"
            
                if (isFortifyScan == "true" || env.BRANCH_NAME == 'release') {
                //if (isFortifyScan == "true" || env.BRANCH_NAME == 'DevOps_test_npx2_paynow') {
                
                            echo 'calling fortify scan on different agent!!'
                            
                            checkout scm
                            
                            getDevRecipients ()
                            echo 'PATH  '+env.PATH
                            def fprFileName = "${filename}"+".fpr"
                            def pdfFileName = "${filename}"+".pdf"
                            def pdfWorkBook = "${ProjectName}"+"_devbook_"+env.BUILD_NUMBER+".pdf"
                            sh "echo Filename: $filename"
                            sh 'sourceanalyzer -clean'
                            sh "sourceanalyzer -b ${ProjectName} -logfile 'logtest.txt' './src/main/java/**/*.java' -source '1.8'"
                            sh "sourceanalyzer -64 -b ${ProjectName} -format fpr -f ${fprFileName} -scan"
                            sh "BIRTReportGenerator -template 'OWASP Top 10' -format 'PDF'  -output ${pdfFileName} -source ${fprFileName}" 
                            sh "BIRTReportGenerator -template 'Developer Workbook' -format 'PDF'  -output ${pdfWorkBook} -source ${fprFileName}" 
                            
                                    if (env.sonarAnalyze == "true") {
                                
                                        withCredentials([
                                            string( credentialsId: 'sonar_auth_token', variable: 'sonar_auth_token'),
                                            string( credentialsId: 'ssc_auth_token', variable: 'ssc_auth_token')
                                            ]) {
                                            
                                            sh script: "mvn sonar:sonar \
                                                -Dsonar.login=${sonar_auth_token} \
                                                -Dsonar.projectName=${ProjectName} \
                                                -Dsonar.projectVersion=${env.BRANCH_NAME}_${env.BUILD_NUMBER} \
                                                -Dsonar.fortify.ssc.url=http://authToken:${ssc_auth_token}@${env.SSCHOST} \
                                                -Dsonar.fortify.ssc.appversion=${ProjectName}:1.0 \
                                                -Dsonar.fortify.ssc.uploadFPR=${fprFileName} \
                                                -Dsonar.fortify.ssc.failOnArtifactStates=SCHED_PROCESSING,PROCESSING \
                                                -Dsonar.fortify.ssc.processing.timeout=180 \
                                                -Dsonar.host.url=${env.SONARURL} \
                                                -Dsonar.java.binaries=."
                                        }
                                    }
                                    
                            emailext attachmentsPattern: '**/*.fpr, **/*.pdf', body: 'Attached reports on fortify code scan', subject: "$ProjectName Fortify Scan report", to: "${tolist}"
                        }
                        
                        else {
                            
                            echo "SCA skipped!"
                        }    
                
                    }
                } 
        }

        post {  

             failure { 
                getDevRecipients()
                emailext (
                subject: "ERROR: Build failure notification for project ${env.JOB_NAME} branch",
                to: "${tolist}",
                body: "Following build failed due to failure in Static Code Analysis. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL}"
                )
             }  
             fixed {  
                getDevRecipients()
                emailext (
                subject: "Resolved: Build success notification for project: ${env.JOB_NAME} branch",
                to: "${tolist}",
                body: "Static Code Analysis successfully executed after previous failure. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL}"
                )
             }  
            
        }
    }    
    
    stage ('Unit Test'){
    agent { label 'master' }
    
        steps {   
            disableRestart ("${STAGE_NAME}")
            script {
                sh 'chmod +x gradlew'
                sh './gradlew test'
                junit 'build/test-results/test/*.xml'  
                step( [ $class: 'JacocoPublisher' ] )
            }                
        }

        post {  

             failure { 
                getDevRecipients()
                emailext (
                subject: "ERROR: Build failure notification for project ${env.JOB_NAME} branch",
                to: "${tolist}",
                body: "Following build failed due to unit test failure. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL}"
                )
             }  
             fixed {  
                 getDevRecipients()
                emailext (
                subject: "Resolved: Build success notification for project: ${env.JOB_NAME} branch",
                to: "${tolist}",                
                body: "Unit test was successfully executed after previous failure. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL}"
                )
             }  
        }  
    }

    stage('Packaging') {
    agent { label 'master' }
        when { branch 'release' }
        //when { branch 'DevOps_test_npx2_paynow' }
        steps {      
            disableRestart ("${STAGE_NAME}")
            
                script{
                    if(prev_build_number==null){
                    prev_build_number = getpreviousbuildnumber()
                    }
                }
            
                script {
                    echo 'Package deployment file and upload to Artifactory: '+env.BUILD_NUMBER
                    
                    echo 'build number '+env.BUILD_NUMBER
                    echo 'Job Name'+env.JOB_NAME
                    
                    createprofile(filename)
                    sh "echo printing actual filename with job number ${filename}"
                    
                    def files = findFiles glob: "target/*.*"
                    PACKAGE_NAME = ProjectName+'_'+env.BUILD_NUMBER+'.tar.gz'
                    echo 'PACKAGE NAME: '+PACKAGE_NAME
                    boolean exists = files.length > 0
                     if(true){
                    
                    echo "Creatig the archive.."
                    sh "tar -cvf ${PACKAGE_NAME} target"
                    
                    def server = Artifactory.newServer url: env.ArtifactoryServer, credentialsId:'ArtifactoryUATServerKey'
                    server.bypassProxy = true
                    def uploadSpec = """{
                            "files": [
                                {
                                    "pattern": "${PACKAGE_NAME}",
                                    "target": "${artifactrepo}/${ProjectName}/"
                                }
                            ]
                    }"""
                                
                    def buildInfo = Artifactory.newBuildInfo()
                    buildInfo.name = "$ProjectName"                    
                    buildInfo.number = env.BUILD_NUMBER
                    server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: true                    
                    server.publishBuildInfo buildInfo
                    echo 'Package uploaded with version '+env.BUILD_NUMBER
                    
                    }
                    
                }
                
                script {
                
                    if (currentBuild.currentResult == "SUCCESS") {
                        echo "Tagging the source code from the build number ${env.BUILD_NUMBER}"
                        sh "git tag $tagname -m 'Created by Jenkins release branch build number ${env.BUILD_NUMBER}'"
                        sh "git push origin $tagname"
                    }
                    
                }
            }
            
        post {  
                 failure { 
                    script{
                        if (currentBuild.currentResult == "FAILURE") {
                            getDevRecipients()
                            emailext (
                            subject: "ERROR: Could not create package from ${env.JOB_NAME} branch",
                            to: "${tolist}",
                            body: "Failed to create build package from following build. Please check build logs for more detials. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL} \nBuild Package: ${PACKAGE_NAME}",
                            attachmentsPattern: "error.log"
                            )
                        }
                    }
                 }
            } 
    }


    stage ('SIT Deployment') {
    agent { label 'master' }
    
        when { branch 'release' }
        //when { branch 'DevOps_test_npx2_paynow' }
     
        environment {
            deploy_environment = 'SIT'
        }

            steps {   
                script {
                
                validaterestartstage(prev_build_number)
                
                if(prev_build_number==null){
                prev_build_number = getpreviousbuildnumber()
                }        
                
                if(validaterestartstage(prev_build_number)==true){        
                    echo 'validaterestartstage was true here.'            
                    mainbuildnumber = packageFromMainBuild("${deploy_environment}", "${prev_build_number}")        
                }
                
                else 
                {
                    mainbuildnumber = env.BUILD_NUMBER
                    echo "Not a restarted Job. proceeding with currentBuild number."
                }
                
                PACKAGE_NAME = ProjectName+'_'+mainbuildnumber+'.tar.gz'            
                echo "${deploy_environment} deployment package: ${PACKAGE_NAME}"                        

                echo "Deploying artifacts from build number: ${PACKAGE_NAME}"
                echo "Deploy to ${deploy_environment} server"
                            
                    withCredentials([
                                    sshUserPrivateKey( credentialsId: 'npx2_sit_server3_key', usernameVariable: 'USERNAME', keyFileVariable: 'SSHKEY'),
                                    string( credentialsId: 'ssh_auth_key_passphrase', variable: 'passphrase'),
                                    string( credentialsId: 'puppet-user', variable: 'artifactory_key')
                                    ]) {
                                    
                    remote = [:]
                    remote.name = "NPX2_SIT_Server"
                    remote.host = env.NPX2_SIT_Server
                    remote.user = USERNAME
                    remote.identityFile = SSHKEY
                    remote.passphrase = passphrase
                    remote.allowAnyHosts = true
                    echo 'Deployment trigger at remote !'
                    cmd = "./PuppetAppDeploymentScripts/${ProjectName}_deploy.sh ${PACKAGE_NAME} ${deploy_environment} ${artifactory_key}"
                    sshCommand remote: remote, command: cmd
                    
                    
                    }
                    
                if (currentBuild.currentResult == "SUCCESS") {
                    echo "${deploy_environment} deployment completed!"
                    echo "Updating build description"
                    updateBuildDescription("${deploy_environment}",prev_build_number,mainbuildnumber)
                    
                    echo "Getting checksum of ${PACKAGE_NAME}"
                    checksum = getCheckSum(PACKAGE_NAME)

                    echo "Sending email notification"
                    sendDeployNotification("${mainbuildnumber}","${deploy_environment}","${PACKAGE_NAME}","${checksum}")
                }    
            }
        }

        post {  
        
            failure { 
               script{
                   //if (currentBuild.currentResult == "FAILURE") {
                       writeDeployOutput("${deploy_host_identifier}")
                       getDevRecipients()
                       emailext (
                       subject: "ERROR: ${deploy_environment} deployment failure notification for project ${env.JOB_NAME} branch",
                       to: "${tolist}",
                       body: "Failed to deploy following build package into ${deploy_environment}. Please check attached logs for more details. if not check Jenkins build logs. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL} \nBuild Package: ${PACKAGE_NAME}",
                       attachmentsPattern: "error.log"
                       )
                   //}
               }
            }  
        }          
    }


    stage ('UAT Deployment') {
    agent none
    
        when { branch 'release' }
        //when { branch 'DevOps_test_npx2_paynow' }
     
        environment {
            deploy_environment = 'UAT'
        }
        
        steps {   
            script {

                node('master') {
                
                    validaterestartstage(prev_build_number)
                    
                    if(prev_build_number==null){
                    prev_build_number = getpreviousbuildnumber()
                    }        
                    
                    if(validaterestartstage(prev_build_number)==true){
                        echo 'validaterestartstage was true here'
                        mainbuildnumber = packageFromMainBuild("${deploy_environment}", "${prev_build_number}")    
                        
                    }
                    
                    else 
                    {
                        mainbuildnumber = env.BUILD_NUMBER
                        echo "Not a restarted Job. proceeding with currentBuild number."
                    }
                    
                    PACKAGE_NAME = ProjectName+'_'+mainbuildnumber+'.tar.gz'
                    echo "${deploy_environment} deployment package: ${PACKAGE_NAME}"
                    
                }

                def options = [:]
                     options['message'] = "Press OK to initiate ${deploy_environment} deployment"
                        notifyAwaitApproval approvers: getApprovers(options),
                            message: "Press OK to initiate ${deploy_environment} deployment",                        
                            emailPrompt: "Build ${currentBuild.description} is ready to deploy to ${deploy_environment}."

                node('master') {
                
                    echo "Deployment to ${deploy_environment} started!"
                            
                        withCredentials([
                                        sshUserPrivateKey( credentialsId: 'npx2_uat_server_key', usernameVariable: 'USERNAME', keyFileVariable: 'SSHKEY'),
                                        string( credentialsId: 'ssh_auth_key_passphrase', variable: 'passphrase'),
                                        string( credentialsId: 'puppet-user', variable: 'artifactory_key')
                                        ]) {
                                        
                    remote = [:]
                    remote.name = "NPX2_UAT_Server"
                    remote.host = env.NPX2_UAT_Server
                    remote.user = USERNAME
                    remote.identityFile = SSHKEY
                    remote.passphrase = passphrase
                    remote.allowAnyHosts = true
                    echo 'Deployment trigger at remote !'
                    def cmd = "./PuppetAppDeploymentScripts/${ProjectName}_deploy.sh ${PACKAGE_NAME} ${deploy_environment} ${artifactory_key}"
                    sshCommand remote: remote, command: cmd
                    
                    
                    }
                    
                        if (currentBuild.currentResult == "SUCCESS") {        
                            echo "${deploy_environment} deployment completed!"
                            updateBuildDescription("${deploy_environment}",prev_build_number,mainbuildnumber)
                            
                            echo "Getting checksum of ${PACKAGE_NAME}"
                            checksum = getCheckSum(PACKAGE_NAME) 
                            
                            echo "Sending email notification"
                            sendDeployNotification("${mainbuildnumber}","${deploy_environment}","${PACKAGE_NAME}","${checksum}")
                        }
                }
            
            }            
      
        }
        
        post {  
            
            failure { 
            
                 node('master') {     
                    script{
                        if (currentBuild.currentResult == "FAILURE") {
                            writeDeployOutput("${deploy_host_identifier}")
                            emailext (
                            subject: "ERROR: ${deploy_environment} deployment failure notification for project ${env.JOB_NAME} branch",
                            to: "${L2Email}",
                            body: "Failed to deploy following build package into ${deploy_environment}. Please check attached logs for more details. if not check Jenkins build logs. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL} \nBuild Package: ${PACKAGE_NAME}",
                            attachmentsPattern: "error.log"
                            )
                        }
                    }
                 }  
            }    
    
        }
        
    }

    stage("PROD Deployment") {
    agent none
        
        when { branch 'release' }
		//when { branch 'DevOps_test_npx2_paynow' }

        environment {
            deploy_environment = 'PROD'
        }
        
        steps {
            script {
        
                node('master') {
                
                    validaterestartstage(prev_build_number)
                    
                    if(prev_build_number==null){
                    prev_build_number = getpreviousbuildnumber()
                    }        
                    
                    if(validaterestartstage(prev_build_number)==true){    
                        echo 'validaterestartstage was true here'
                        mainbuildnumber = packageFromMainBuild("${deploy_environment}", "${prev_build_number}")    
                        echo "    mainbuildnumber is ${mainbuildnumber}"
                    }
                    
                    else 
                    {
                        mainbuildnumber = env.BUILD_NUMBER
                        echo "Not a restarted Job. proceeding with currentBuild number."
                    }

                    PACKAGE_NAME = ProjectName+'_'+mainbuildnumber+'.tar.gz'
                    echo "${deploy_environment} deployment package: ${PACKAGE_NAME}"
                    
                }            

                def options = [:]
                    options['message'] = "Press OK to initiate ${deploy_environment} deployment"
                        notifyAwaitApproval approvers: getApprovers(options),
                                  message: "Press OK to initiate ${deploy_environment} deployment",                        
                                  emailPrompt: "Build ${currentBuild.description} is ready to deploy to ${deploy_environment}."
                              
                node('master') {
                                        
                    echo "Deployment to ${deploy_environment} started!"
                    
                    echo "Deleting local copy of package if exists..."
                        
                        if (fileExists(PACKAGE_NAME)) {
                            echo "Package ${PACKAGE_NAME} already exists!. Deleting it."
                            fileOperations([fileDeleteOperation(excludes: '', includes: "${PACKAGE_NAME}")])
                        }
                        else {
                            echo "${PACKAGE_NAME} does not exists!"
                        }    
                    
                    echo "Downloading the deployment package: ${PACKAGE_NAME} from artifactory"
                            
                            def server = Artifactory.newServer url: env.ArtifactoryServer, credentialsId:'ArtifactoryUATServerKey'
                            server.bypassProxy = true
                                def downloadSpec = """{
                                 "files": [
                                  {
                                      "pattern": "${artifactrepo}/${ProjectName}/${PACKAGE_NAME}",
                                      "target": "",
                                      "flat": "true"
                                    }
                                 ]
                            }"""                
                                server.download spec: downloadSpec, failNoOp: true
                                
                    echo "Getting checksum of ${PACKAGE_NAME}"
                    checksum = getCheckSum(PACKAGE_NAME)
                    
                            sshPublisher(
                                failOnError: true,
                                continueOnError: true,
                                publishers: [
                                    sshPublisherDesc(
                                        configName: 'SFTP-Deployment-Package-Hosting',
                                        transfers: [
                                            sshTransfer(
                                                sourceFiles: "${PACKAGE_NAME}",
                                                removePrefix: '',
                                                remoteDirectory: "DeployPackage/${JOB_NAME}/${mainbuildnumber}"
                                            )
                                        ],
                                        
                                        sshRetry: [
                                            retries: 2
                                        ]
                                        
                                    )
                                ]
                            )    
            
                if (currentBuild.currentResult == "SUCCESS") {
                echo "${deploy_environment} package successfully copied to sftp server, path: "+"${prod_sftp_absolute_path}/${JOB_NAME}/${mainbuildnumber}"
                    updateBuildDescription("${deploy_environment}",prev_build_number,mainbuildnumber)
                    
                    echo "Sending email notification"
                    sendUploadNotification("${mainbuildnumber}","${deploy_environment}","${PACKAGE_NAME}","${checksum}")
                }
                
            }
          }  
                              
        }
        
        post {  
            
            failure { 
                node('master') {                
                    script{
                        if (currentBuild.currentResult == "FAILURE") {
                            writeDeployOutput('SSH')
                            emailext (
                            subject: "ERROR: ${deploy_environment} deployment failure notification for project ${env.JOB_NAME} branch",
                            to: "${L2Email}",
                            body: "Failed to copy following build package into ${deploy_environment} SFTP. Please check Jenkins build logs for more detials. \n\nProject/Branch: ${env.JOB_NAME}. \nBuild Number: ${env.BUILD_NUMBER} \nBuild URL: ${env.BUILD_URL} \nBuild Package: ${PACKAGE_NAME}"
                            )
                        }
                    }
                 }
            }
        }    
      }
    }
}

def getApproversList(options) {
  
  def rawApprovers = env.L2Email
    switch(rawApprovers) {
      case String:
        // already csv
        return rawApprovers
      case Map:
        // keys are usernames and values are names
        return rawApprovers.keySet().join(',')
      case ArrayList:
        return rawApprovers.join(',')
      default:
        throw new Exception("Unexpeced approver type ${rawApprovers.class}!")        
    }
}

def getApprovers(options) {
def rawApprovers = "${L2Email}"
def csvApproverUsernames = { getApproversList(options) }()

    node {
        emailext body: "Build: ${env.JOB_NAME} Build Number: ${env.BUILD_NUMBER} Action is required to proceed ${STAGE_NAME}, click here to continue:  ${env.JOB_URL}", 
        to: rawApprovers, subject: "Action Required for Build ${env.JOB_NAME} (#${env.BUILD_NUMBER})"
        
    }
   milestone()
  input message: options.message,
        submitter: csvApproverUsernames
  milestone()
    
}


import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
def notifyAwaitApproval(role) {    
    def users = [:]
    def sidsstring = "${L2Email}"
    sids = sidsstring.split(",")
    echo 'List of users '+sids
    sids.each { sid ->
    users[sid] = Jenkins.instance.getUser(sid).fullName }
    return users 
}


def getpreviousbuildnumber() {
    def prev_build_number = 0   
    
    def temp = currentBuild.getBuildCauses("org.jenkinsci.plugins.pipeline.modeldefinition.causes.RestartDeclarativePipelineCause")   
    
    def buildShortDesc = temp['shortDescription']
    
    if(buildShortDesc != null)
    {
        def strbuildShortDesc = buildShortDesc.toString()
        echo 'temp shortDescription inside if '+strbuildShortDesc
        if(strbuildShortDesc.contains("#"))
        {
                    echo 'string contains # value '+strbuildShortDesc.indexOf("#")    
                            def index1 = strbuildShortDesc.indexOf("#")
                            def index2 = strbuildShortDesc.indexOf(",")
                            prev_build_number=strbuildShortDesc.substring((index1+1), strbuildShortDesc.indexOf(","))
                            echo 'Prev build number ' +prev_build_number
        }
    }   
    echo "getpreviousbuildnumber returned ${prev_build_number}"
    return prev_build_number
}



def disableRestart(stageName) {
    restartedFromStage = currentBuild.getBuildCauses().any { cause ->
        cause._class == 'org.jenkinsci.plugins.pipeline.modeldefinition.causes.RestartDeclarativePipelineCause'
        }
            if (restartedFromStage) {
                        error "Restarting from $stageName stage is disabled. Restarting allowed only from deployment stages!"
        }
}


def sendDeployNotification (mainbuildnumber,environment,PACKAGE_NAME,checksum) {

    getJobDetails ()
    
    if (environment == 'SIT') {
        rawApprovers = getDevRecipients()
        emailext body:"Following package successfully deployed in $environment.\n\n\
        Project: ${ABS_JOB_NAME}\n\
        Branch: ${env.BRANCH_NAME}\n\
        Build Number: $mainbuildnumber\n\
        Deployed Package: ${PACKAGE_NAME}\n\
        Sha256sum: ${checksum}",
        
        to: rawApprovers, subject: "Deploy Notification: ${env.JOB_NAME} (#${mainbuildnumber})"
    }
    else {
        rawApprovers = "${L2Email}"
        emailext body:"Following package successfully deployed in $environment.\n\n\
        Project: ${ABS_JOB_NAME}\n\
        Branch: ${env.BRANCH_NAME}\n\
        Build Number: $mainbuildnumber\n\
        Deployed Package: ${PACKAGE_NAME}\n\
        Sha256sum: ${checksum}",
        
        to: rawApprovers, subject: "Deploy Notification: ${env.JOB_NAME} (#${mainbuildnumber})"
    }
}

def sendUploadNotification (mainbuildnumber,environment,PACKAGE_NAME,checksum) {

    getJobDetails ()
    def rawApprovers = "${L2Email}"

    emailext body:"Following package successfully copied to $environment SFTP.\n\n\
    Project: ${ABS_JOB_NAME}\n\
    Branch: ${env.BRANCH_NAME}\n\
    Build Number: $mainbuildnumber\n\
    SFTP Path: ${prod_sftp_absolute_path}/${JOB_NAME}/${mainbuildnumber}\n\
    Available Package: ${PACKAGE_NAME}\n\
    Sha256sum: ${checksum}",
    
    to: rawApprovers, subject: "Deploy Notification: ${env.JOB_NAME} (#${mainbuildnumber})"
}

def getAvailablePkgs () {
    //def packages = findFiles(glob: 'final_artifacts/*.gz')
    def packages = findFiles(glob: '*.gz')
        if (packages.size() < 1) {
        //throw new Exception("Pakckage not found!")
        println "warning: Pakckage not found!"
        }
    
    def packagestring = packages.join(",")
    //string availableFiles = packagestring.replace("final_artifacts/", "")
      availableFiles = packagestring
        if (!availableFiles?.trim()) {
            //throw new Exception("availableFiles is null or emty")
            println "warning: availableFiles is null or empty"
        }
    println 'Available modules: '+availableFiles    
}

def getJobDetails () {
    def JobNameParts = JOB_NAME.tokenize('/') as String[]
    ABS_JOB_NAME= JobNameParts[0]
    println (ABS_JOB_NAME)
}


def createprofile(packagename) {    
    getJobDetails ()
    echo "Printing ABS Job name from function ${ABS_JOB_NAME}"
    def buildabspath ="${JENKINS_HOME}"+"/jobs/"+"${ABS_JOB_NAME}"+"/branches/"+"${env.BRANCH_NAME}"+"/builds/"+"${BUILD_NUMBER}"
    def inputtext = "PackageVersion="+"${packagename}"+"\nMasterBuildNumber="+"${BUILD_NUMBER}"+"\nSITDeployment=N"+"\nUATDeployment=N"+"\nPRODDeployment=N"
    echo 'Displaying Master job details ' +inputtext
    writeFile file: "${buildabspath}/"+'JenkinsBuildVersion_NETS.txt', text: inputtext
  
}

def updateprofile(env) {
    getJobDetails ()
    def buildabspath ="${JENKINS_HOME}"+"/jobs/"+"${ABS_JOB_NAME}"+"/branches/"+"${env.BRANCH_NAME}"+"/builds/"+"${BUILD_NUMBER}"
    echo 'abspath path '+buildabspath
    try    {
    def result = 'SITDeployment=N\nUATDeployment=N\nPRODDeployment=N'
        switch(env)
        {
            case "SIT":
                result="SITDeployment=Y\nUATDeployment=N\nPRODDeployment=N"
                break
            case "UAT":
                result="SITDeployment=Y\nUATDeployment=Y\nPRODDeployment=N"
                break
            case "PROD":
                result="SITDeployment=Y\nUATDeployment=Y\nPRODDeployment=Y"
            break                
            default:
                result="SITDeployment=N\nUATDeployment=N\nPRODDeployment=N"    
                break
                
        }
        echo 'display value for result '+result
        writeFile file: "${buildabspath}/"+'JenkinsBuildVersion_NETS.txt', text: "PackageName=${PACKAGE_NAME}\nMasterBuildNumber=${BUILD_NUMBER}\n${result}"
    }
    catch(Exception e)
        {
            error 'Method createprofile failed, unable to write file'
        }
         
}

def cascadeprofile(environment, prev_build_number) {
            getJobDetails ()
                    echo ' pre buld number '+prev_build_number
                    def abslastbuildpath ="${JENKINS_HOME}"+"/jobs/"+"${ABS_JOB_NAME}"+"/branches/"+"${env.BRANCH_NAME}"+"/builds/"+"${BUILD_NUMBER}"
                    def strPreBuild = prev_build_number
                    echo ' pre buld number  string value '+prev_build_number
                    def abslastbuildfilepath ="${JENKINS_HOME}"+"/jobs/"+"${ABS_JOB_NAME}"+"/branches/"+"${env.BRANCH_NAME}"+"/builds/"+"${strPreBuild}"
                    def sourceFile = abslastbuildfilepath+"/"+"JenkinsBuildVersion_NETS.txt"
                    def destinationFolderPath = "${JENKINS_HOME}"+"/jobs/"+"${ABS_JOB_NAME}"+"/branches/"+"${env.BRANCH_NAME}"+"/builds/"+"${BUILD_NUMBER}"
                    File  destinationFolder = new File(abslastbuildpath)
                    echo 'File copying'+sourceFile
                    if (fileExists(sourceFile)){
                    echo 'File exists so copying'
                    sh "cp -f ${sourceFile} ${destinationFolderPath}"                
                    }
                    //updateprofile(environment)
}

def originalbuildnumber() {
    getJobDetails ()
    def buildabspath ="${JENKINS_HOME}"+"/jobs/"+"${ABS_JOB_NAME}"+"/branches/"+"${env.BRANCH_NAME}"+"/builds/"+"${BUILD_NUMBER}"+'/JenkinsBuildVersion_NETS.txt'
    echo 'abspath path '+buildabspath
    
    File file = new File(buildabspath)
    def lines = file.readLines()
    //readfile file: "${buildabspath}/"+'JenkinsBuildVersion_NETS.txt'
    echo 'Main build number ' +lines[1]
    return lines;
}

def validaterestartstage(buildno) {
    
    echo '    validaterestartstage called ' +buildno
    if(buildno!=null && buildno.toInteger()>0)
    {
        echo '    validaterestartstage returning value ' 
        return true
    }
    else 
    {
        echo '    validaterestartstage end ' 
        return false
    }
}

def updateBuildDescription(environment, prev_build_number, mainbuildnumber) {

            if(currentBuild.description == null || currentBuild.description == "")
            {                
                    echo "prev_build_number is ${prev_build_number}"
                    
                if (prev_build_number == 0 && validaterestartstage(prev_build_number)==false) {
                echo "Updating build description as current build number: ${env.BUILD_NUMBER}"
                currentBuild.description="Version "+env.BUILD_NUMBER +" deployed in ${environment}"
                }
                
                else if (prev_build_number == 0 && validaterestartstage(prev_build_number)==true) {
                echo "Updating build description as main build number: ${mainbuildnumber}"
                currentBuild.description="Version "+mainbuildnumber +" deployed in ${environment}"
                }
                
                else if (prev_build_number != 0 && validaterestartstage(prev_build_number)==true) {
                echo "prev_build_number not equel 0, hence, Updating build description as main build number: ${mainbuildnumber}"
                currentBuild.description="Version "+mainbuildnumber +" deployed in ${environment}"
                }
                
                else {
                echo "Updating build description as previous build number: ${prev_build_number}"
                currentBuild.description="Version "+prev_build_number +" deployed in ${environment}"
                }
            }
            
            else 
            
            {
                echo "currentBuild.description was emty. hence, Updating build description as previous main build number ${mainbuildnumber}"
                currentBuild.description=currentBuild.description+"\nVersion "+mainbuildnumber +" deployed in ${environment}"
            }    
}

def packageFromMainBuild(environment,prev_build_number) {
        cascadeprofile(environment, prev_build_number)
        def packageinfo = originalbuildnumber()
        echo "packageinfo is ${packageinfo}"
        mainpackagename = packageinfo[0]
        PACKAGE_NAME = mainpackagename
        mainbuildnumber = packageinfo[1]    
        def arryMainBuildnumber = mainbuildnumber.split('=')
        mainbuildnumber = arryMainBuildnumber[1]                
        echo "${environment} deployment, Main build: ${mainbuildnumber}"
        
    return mainbuildnumber
}

import groovy.json.JsonSlurper
def getCheckSum (PACKAGE_NAME) {
        def response = httpRequest httpMode: 'GET',
                       authentication: 'ArtifactoryUATServerKey',
                       ignoreSslErrors: 'true',
                       consoleLogResponseBody: 'false',
                       validResponseCodes: '100:500', //Process HTTP erros here to avoild build failures
                       timeout: 5,
                       url: "${env.ArtifactoryServer}/api/storage/${artifactrepo}/${ProjectName}/${PACKAGE_NAME}"

        println('Status: '+response.status)

            if (response.status == 200) {
                //println('Response: '+response.content)                    
                JsonSlurper slurper = new JsonSlurper()
                Map responseJson = slurper.parseText(response.content)
                println (responseJson)
                
                boolean isShaPresent = responseJson.get("checksums").keySet().contains("sha256")
                    if (!isShaPresent) {
                    println "Could not get the checksum, not failing the buld at this stage!"
                    checksum = "Please get the checksum from artifactory."
                    }
                    
                    else {
                    checksum = responseJson.get("checksums").get("sha256")
                    println 'sha256 checksum is '+(checksum)
                    }
            }

            else {
                println 'Get checksum failed'
                println "Setting checksum message, not failing the buld at this stage!"
                checksum = "Please get the checksum from artifactory."
            }
            
    return checksum
}

def getDevRecipients () {
    tolist = emailextrecipients([[$class: 'CulpritsRecipientProvider'],[$class: 'RequesterRecipientProvider'],[$class: 'DevelopersRecipientProvider'],[$class: 'UpstreamComitterRecipientProvider']])
        if (tolist == null || tolist == "") {
            echo "Could not find matching recipient list. collecting the system default email ${env.system_default_email}"
            tolist = "${env.system_default_email}"
        }
 return tolist
}

def writeDeployOutput(searchPrefix) {    
    getJobDetails ()
    def errorLog = 'error.log'
    buildlogspath ="${JENKINS_HOME}"+"/jobs/"+"${ABS_JOB_NAME}"+"/branches/"+"${env.BRANCH_NAME}"+"/builds/"+"${BUILD_NUMBER}"
        if (fileExists(errorLog)) {
            new File(errorLog).delete()
        }
    sh "grep ${searchPrefix} ${buildlogspath}/log > ${errorLog} || :"
}
