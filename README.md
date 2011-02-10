This project is a Java framework and library for automated testing.
It provides functionality to enable testing of web-based applications
(using [selenium](http://seleniumhq.org/) ), GNOME apps (using LDTP), and
console applications.

[TestNG](http://testng.org) is used as the test harness.  There are
several plugins available here for TestNG to facilitate logging,
screenshots, and interactions with Bugzilla and TCMS.

Running tests under Hudson is also supported.

FAQ:

1) How do I run tests under hudson? 

   We use leiningen to build our automation, which will include
   webui-framework as a dependency.  Then it's just a simple Bash
   build step that calls java:

    java -cp `lein classpath` org.testng.TestNG [options] [suitefile]
    

2) How do I get pretty reports in Hudson?  

   Specify the Reportng html listener as a testng option on the
   command line (can also be added to the suite file):
 
     -listener org.uncommons.reportng.HTMLReporter

   In your java logging settings (default settings location is
   ~/log.properties), add

     com.redhat.qe.auto.testng.TestNGReportHandler

   to the list of handlers.

   Several java system properties may need to be set (this can be done
   on the command line or via properties file whose default location
   is ~/automation.properties):
 
     testng.outputdir=[any relative path within the workspace - must
     	  be on the list of dirs to archive for the hudson job,
     	  defaults to the dir where the jvm is invoked (same as
     	  user.dir system property]
     org.uncommons.reportng.escape-output=false
     org.uncommons.reportng.stylesheet=web/reportng-custom.css

    
   (For jobs that can take screenshots, currently only selenium):

     selenium.screenshot.dir=[any subdir of the outputdir]    
     selenium.screenshot.link.path=../artifact/[screenshotdir]

   Install the HTML Reports plugin for hudson.  In the
   Post-build actions for your hudson job, point the "Publish HTML
   Reports" section to the [outputdir]/html/index.html file that will
   be produced by the TestNG report.

   The report will use icons that need to be served by the hudson web
   server.  Create a subdir images/webui-framework under the 'war' dir
   (for standalone hudson - for tomcat, create it under 'webapps').
   Copy the files resources/web/*.png from this repository, into the
   subdirectory you created.  Then the report's icons will link to the
   correct files.
   	  
3) How do I use kerberos negotiate auth (with services like TCMS)?
   First you'll need to get a kerberos ticket with kinit. Then you'll
   need the following jvm arguments when running your tests:

     -Duser.krb5cc=[your-kerb-cache-file] -Djavax.security.auth.useSubjectCredsOnly=false 
   
   Your cache file is something like /tmp/krb5cc_500.  The number at
   the end is your unix userid, check your /tmp dir for files like
   this - the one owned by you should be the one you want.
