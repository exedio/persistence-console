#!'groovy'
import groovy.transform.Field
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

@Field
String jdk = 'openjdk-11'
@Field
String idea = '2023.2'
@Field
String ideaSHA256 = 'b1a5c267ca86850764b0541bee0c27af7d2082e55516e95a0c8d30539571735c'

String projectName = env.JOB_NAME.substring(0, env.JOB_NAME.indexOf("/")) // depends on name and location of multibranch pipeline in jenkins
boolean isRelease = env.BRANCH_NAME=="master"

Map<String, ?> recordIssuesDefaults = [
	failOnError         : true,
	enabledForFailure   : true,
	ignoreFailedBuilds  : false,
	skipPublishingChecks: true,
	qualityGates        : [[threshold: 1, type: 'TOTAL', unstable: true]],
]

properties([
		gitLabConnection(env.GITLAB_CONNECTION),
		buildDiscarder(
			logRotator(
				numToKeepStr         : isRelease ? '1000' : '30',
				artifactNumToKeepStr : isRelease ?  '100' :  '2',
		))
])

boolean tryCompleted = false
try
{
	Map<String, Closure<?>> parallelBranches = [:]

	parallelBranches["Main"] = {
		nodeCheckoutAndDelete { scmResult ->
			def buildTag = makeBuildTag(scmResult)

			mainImage(imageName("Main")).inside(dockerRunDefaults()) {
				ant 'clean jenkins' +
				    ' "-Dbuild.revision=${BUILD_NUMBER}"' +
				    ' "-Dbuild.tag=' + buildTag + '"' +
				    ' -Dbuild.status=' + (isRelease?'release':'integration') +
				    ' -Dinstrument.verify=true' +
				    ' -Dtomcat.port.shutdown=' + port(0) +
				    ' -Dtomcat.port.http=' + port(1)
			}

			recordIssues(
				*: recordIssuesDefaults,
				tools: [
					java(),
				],
			)
			junit(
				allowEmptyResults: false,
				testResults: 'build/testresults/*.xml',
				skipPublishingChecks: true
			)
			archiveArtifacts fingerprint: true, artifacts: 'build/success/*'
			archiveArtifacts 'build/testprotocol.*,build/*.log,tomcat/logs/*,build/testtmpdir'
			plot(
				csvFileName: 'plots.csv',
				exclZero: false,
				keepRecords: false,
				group: 'Sizes',
				title: 'exedio-cope-console.jar',
				numBuilds: '150',
				style: 'line',
				useDescr: false,
				propertiesSeries: [
					[ file: 'build/exedio-cope-console.jar-plot.properties', label: 'exedio-cope-console.jar' ]
				],
			)
		}
	}

	parallelBranches["Forensic"] = {
		nodeCheckoutAndDelete {
			discoverGitReferenceBuild()
			gitDiffStat()
			if (isRelease || env.BRANCH_NAME.contains("forensic")) mineRepository()

			recordIssues(
				*: recordIssuesDefaults,
				qualityGates: [[threshold: 1, type: 'TOTAL_HIGH', unstable: true]],
				tools: [
					taskScanner(
						excludePattern:
							'.git/**,lib/**,' +
							// binary file types
							'**/*.jar,**/*.zip,**/*.tgz,**/*.jpg,**/*.gif,**/*.png,**/*.tif,**/*.webp,**/*.pdf,**/*.eot,**/*.ttf,**/*.woff,**/*.woff2,**/keystore',
						// causes build to become unstable, concatenation prevents matching this line
						highTags: 'FIX' + 'ME',
						// does not cause build to become unstable
						normalTags: 'TODO',
						ignoreCase: true),
				],
			)
		}
	}

	parallelBranches["Idea"] = {
		nodeCheckoutAndDelete {
			def ideaImage = docker.build(
				imageName('Idea'),
				'--build-arg JDK=' + jdk + ' ' +
				'--build-arg IDEA=' + idea + ' ' +
				'--build-arg IDEA_SHA256=' + ideaSHA256 + ' ' +
				'conf/idea')
			ideaImage.inside(dockerRunDefaults()) {
				ant "src"
				shSilent "/opt/idea/bin/inspect.sh " + env.WORKSPACE + " 'Project Default' idea-inspection-output"
			}
			archiveArtifacts 'idea-inspection-output/**'
			shSilent "rm idea-inspection-output/SpellCheckingInspection.xml"
			shSilent "rm idea-inspection-output/GrazieInspection.xml" // grammar and style; settings/exclusions are stored in IDE and not in project
			// replace project dir to prevent UnsupportedOperationException - will not be exposed in artifacts
			shSilent "find idea-inspection-output -name '*.xml' | " +
			         "xargs --no-run-if-empty sed --in-place -- 's=\\\$PROJECT_DIR\\\$=" + env.WORKSPACE + "=g'"
			recordIssues(
				*: recordIssuesDefaults,
				tools: [ideaInspection(pattern: 'idea-inspection-output/**')],
			)
		}
	}

	parallelBranches["Ivy"] = {
		def cache = 'jenkins-build-survivor-' + projectName + "-Ivy"
		lockNodeCheckoutAndDelete(cache) {
			mainImage(imageName('Ivy')).inside(
				dockerRunDefaults('bridge') +
				"--mount type=volume,src=" + cache + ",target=/var/jenkins-build-survivor") {
				ant "-buildfile ivy" +
				    " -Divy.user.home=/var/jenkins-build-survivor"
			}
			archiveArtifacts 'ivy/artifacts/report/**'

			assertGitUnchanged()

			// There should be an assertIvyExtends for each <conf name="abc" extends="def" /> in ivy/ivy.xml.
			assertIvyExtends("runtime", "compile")
			assertIvyExtends("test", "runtime")
			assertIvyExtends("ide", "runtime")
			assertIvyExtends("ide", "test")
		}
	}

	parallel parallelBranches

	tryCompleted = true
}
finally
{
	if (!tryCompleted)
		currentBuild.result = 'FAILURE'

	// workaround for Mailer plugin: set result status explicitly to SUCCESS if empty, otherwise no mail will be triggered if a build gets successful after a previous unsuccessful build
	if (currentBuild.result == null)
		currentBuild.result = 'SUCCESS'
	node('email') {
		step(
			[$class                  : 'Mailer',
			 recipients              : emailextrecipients([isRelease ? culprits() : developers(), requestor()]),
			 notifyEveryUnstableBuild: true])
	}
	// https://docs.gitlab.com/ee/api/commits.html#post-the-build-status-to-a-commit
	updateGitlabCommitStatus state: currentBuild.resultIsBetterOrEqualTo("SUCCESS") ? "success" : "failed"
}

// ------------------- LIBRARY ----------------------------
// The code below is meant to be equal across all projects.

void lockNodeCheckoutAndDelete(String resource, Closure body)
{
	lock(resource) {
		nodeCheckoutAndDelete(body)
	}
}

void nodeCheckoutAndDelete(@ClosureParams(value = SimpleType, options = ["Map<String, String>"]) Closure body)
{
	node('GitCloneExedio && docker') {
		env.JENKINS_OWNER = shStdout("id --user") + ':' + shStdout("id --group")
		try
		{
			deleteDir()
			def scmResult = checkout scm
			updateGitlabCommitStatus state: 'running'

			body.call(scmResult)
		}
		finally
		{
			deleteDir()
		}
	}
}

String jobNameAndBuildNumber()
{
	env.JOB_NAME.replace("/", "-").replace(" ", "_") + "-" + env.BUILD_NUMBER
}

def mainImage(String imageName)
{
	return docker.build(
		imageName,
		'--build-arg JDK=' + jdk + ' ' +
		'--build-arg JENKINS_OWNER=' + env.JENKINS_OWNER + ' ' +
		'conf/main')
}

String imageName(String pipelineBranch, String subImage = '')
{
	String isoToday = new Date().format("yyyyMMdd")
	String name = 'exedio-jenkins:' + jobNameAndBuildNumber() + '-' + pipelineBranch + '-' + isoToday
	if (!subImage.isBlank()) name += '-' + subImage
	return name
}

static String dockerRunDefaults(String network = 'none', String hostname = '')
{
	return "--cap-drop all " +
	       "--security-opt no-new-privileges " +
	       "--network " + network + " " +
	       (hostname != '' ? "--network-alias " + hostname + " " : "") +
	       "--dns-opt timeout:1 " + // seconds; default is 5
	       "--dns-opt attempts:1 " // default is 2
}

String makeBuildTag(Map<String, String> scmResult)
{
	String treeHash = shStdout "git cat-file -p " + scmResult.GIT_COMMIT + " | grep '^tree ' | sed -e 's/^tree //'"
	return 'build ' +
	       env.BRANCH_NAME + ' ' +
	       env.BUILD_NUMBER + ' ' +
	       new Date().format("yyyy-MM-dd") + ' ' +
	       scmResult.GIT_COMMIT + ' ' +
	       treeHash
}

void assertIvyExtends(String extendingConf, String parentConf)
{
	int status = shStatus(
		"LC_ALL=C" +
		" diff --recursive lib/" + parentConf + " lib/" + extendingConf +
		" | grep --invert-match '^Only in lib/" + extendingConf + ": '" +
		" > ivy/artifacts/ivyExtends" + extendingConf + ".txt")
	if (status!=0 && status!=1) // https://www.man7.org/linux/man-pages/man1/diff.1.html
	{
		error 'FAILURE because diff had trouble'
	}
	String result = readFile "ivy/artifacts/ivyExtends" + extendingConf + ".txt"
	if (result != '')
	{
		error 'FAILURE because ivy conf "' + extendingConf + '" does not just add jar-files to "' + parentConf + '":\n' +
		      result
	}
}

void shSilent(String script)
{
	try
	{
		sh script
	}
	catch (Exception ignored)
	{
		currentBuild.result = 'FAILURE'
	}
}

int shStatus(String script)
{
	return (int) sh(script: script, returnStatus: true)
}

String shStdout(String script)
{
	return ((String) sh(script: script, returnStdout: true)).trim()
}

void ant(String script, String jvmargs = '')
{
	shSilent 'ant -noinput ' + script
}

void assertGitUnchanged()
{
	String gitStatus = shStdout "git status --porcelain --untracked-files=normal"
	if (gitStatus != '')
	{
		error 'FAILURE because fetching dependencies produces git diff:\n' + gitStatus
	}
}

def port(int offset)
{
	return 28000 + 10*env.EXECUTOR_NUMBER.toInteger() + offset
}
