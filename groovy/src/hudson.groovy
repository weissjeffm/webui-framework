
/*
 * Script that runs a testng suite or test within a suite
 * Args = xml-suite-file [optional-test-name]
 */


ant = new AntBuilder();

workspace = System.getProperty("workspace.dir", System.getenv("WORKSPACE") ?: System.getProperty("user.dir"))  //defaults to $WORKSPACE, or if that's null, pwd
println("workspace= "+workspace)
println("user.dir= " + System.getProperty("user.dir"))


automationDir = workspace + "/" + (System.getProperty("testng.project.dir", "automatjon/jon"))
webuiFramework = workspace + "/" + (System.getProperty("webui-framework.dir", "webui-framework"))
System.setProperty("automation.dir",automationDir);

//set reportng formatting options
System.setProperty("org.uncommons.reportng.escape-output","false")
System.setProperty("org.uncommons.reportng.stylesheet","${webuiFramework}/web/reportng-custom.css")

/*
 * Now that we know where external jars and classes should be, tell the classloader about them
 * so we can use them dynamically
 */
javacDirs = setupClasspath([webuiFramework, automationDir])

compileProject()

masterXmlFile = args[0]

outputDir = null
testng = Class.forName("org.testng.TestNG").newInstance();
addListeners()	
setOutputDir()

if (args.length > 1) {
	testToRun = args[1]
	def parser = Class.forName("org.testng.xml.Parser").getConstructor(String.class).newInstance(masterXmlFile)
	def masterXmlSuite = parser.parse().iterator().next();
	def ourTest = masterXmlSuite.getTests().find { it.getName() == testToRun }
	
	def ourSuite = Class.forName("org.testng.xml.XmlSuite").newInstance()
	def newTest = Class.forName("org.testng.xml.XmlTest").getConstructor(Class.forName("org.testng.xml.XmlSuite")).newInstance(ourSuite)

	
	//clone the test
	newTest.setXmlPackages(ourTest.getXmlPackages())
	//newTest.setXmlClasses(ourTest.getXmlClasses())
	newTest.setIncludedGroups(ourTest.getIncludedGroups())
	newTest.setExcludedGroups(ourTest.getExcludedGroups())
	newTest.setName(ourTest.getName())
	
	ourSuite.setName("Hudson_Test_Suite")
	testng.setXmlSuites([ourSuite])	
}
else {
	testng.setTestSuites([masterXmlFile])
}

testng.run()
makeJunitReport()
!testng.hasFailure()
/* ----- internal methods ----- */


def compileProject() {
	def binDir = "$automationDir/bin"
	ant.mkdir(dir: binDir)
	ant.javac(srcdir: "$automationDir/src", destdir: binDir, classpath: javacDirs.join(":"))
	
	//<javac srcdir="${test.src.dir}" destdir="${test.build.dir}" classpathref="tests.cp" debug="on" />
	
}

def setOutputDir(){
	//set the output dir
	outputDir = System.getProperty("testng.outputdir", "$automationDir/test-output")
	println("Setting testng output dir to $outputDir")
	testng.setOutputDirectory(outputDir)
	
}
def makeJunitReport(){
	//generate junit report
	junitDir = "${automationDir}/test-output-junit"
	ant.mkdir(dir: junitDir)
	ant.junitreport(todir: junitDir) {
		fileset(dir: outputDir){
			include(name: "**/*.xml")
			exclude(name: "**/testng-failed.xml")
			exclude(name: "xml/**")
		}
	}
}

def addListeners(){
	// the object cast below is so the api doesn't get confused about which overloaded method we're calling
	listeners = ["com.redhat.qe.auto.selenium.TestNGListener", "com.redhat.qe.auto.bugzilla.BugzillaTestNGListener", 
	             "org.uncommons.reportng.HTMLReporter", "org.uncommons.reportng.JUnitXMLReporter"]
	listeners.each {
		println("Adding testng listener $it")
		testng.addListener((Object)Class.forName(it).newInstance());
	}
}

def String pathToFileURL(String path){
	//path = path.replace(":", "") //remove colons from windows drive letter
	path = path.replace("\\", "/") //backslashes to front slash
	
	output = "file:///" + path
	
	//ant.echo("Produced URL $output")
	return output
}


/*
 * Takes array of paths to eclipse projects, reads the .classpath file in each one looking for lib entries.
 * Adds those libs to the running jvm classpath.
 */
def setupClasspath(eclipseProjectDirList){
	 antClasspaths = []
	 println(eclipseProjectDirList)
	 eclipseProjectDirList.each { dir ->
		println("Reading eclipse classpath file in $dir")
		def rootNode = new XmlParser().parse(new File(dir + "/" + ".classpath"))
		def entries = rootNode.classpathentry.findAll { (it.@kind == 'lib' && !it.@path.startsWith("/")) || it.@kind == 'output'}
	 	entries.each {
			def entryDir = dir + "/" + it.@path + (it.@kind == "output" ? "/":"") 
			antClasspaths.add(entryDir)
	 		entry = new URL(pathToFileURL(entryDir)) //append trailing slash to dirs

	 		println("Adding classpath entry $entry")
	 		
	 		//this.class.classLoader.rootLoader.addURL(entry)
	 		 ClassLoader.getSystemClassLoader().addURL(entry) //don't do this for all entries, causes the same libs to be loaded by different cl's, classcastexceptions -jmw
	 	}
	 }
	 return antClasspaths
 }
