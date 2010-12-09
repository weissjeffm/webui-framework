(defproject webui-framework "1.0.0-SNAPSHOT"
  :description "A library of automated testing related functions."
  :java-source-path "src"
  ;  :java-options {:debug "true" }
  :dependencies [[org.apache.xmlrpc/xmlrpc-client "3.1.3"]
		 [commons-httpclient/commons-httpclient "3.1"]
		 [com.google.collections/google-collections "1.0"]
		 [velocity/velocity "1.4"]
		 [org.testng/testng "5.14.2"]
		 [com.trilead/trilead-ssh2 "build213-svnkit-1.3-patch"]
		 [ca.juliusdavies/not-yet-commons-ssl "0.3.11"]
		 [org.clojars.weissjeffm/httpclient-negotiate "1.0"]
		 [commons-logging/commons-logging "1.1.1"]
		 [org.seleniumhq.selenium.client-drivers/selenium-java-client-driver "1.0.2"]]
  :dev-dependencies [ [lein-eclipse "1.0.0"] ])
