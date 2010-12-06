(defproject webui-framework "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :java-source-path "src"
  :java-options {:debug "true" }
  :repositories {"apacherepository" "http://repository.apache.org/snapshots/"}
  :dependencies [[org.apache.xmlrpc/xmlrpc-client "3.1.3"]
		 [org.clojars.kjw/commons-httpclient "3.1"]
		 [com.google.collections/google-collections "1.0"]
		 [velocity/velocity "1.4"]
		 [org.testng/testng "5.14.2"]
		 [com.trilead/trilead-ssh2 "build213-svnkit-1.3-patch"]
		 [ca.juliusdavies/not-yet-commons-ssl "0.3.11"]
		 [org.clojars.weissjeffm/httpclient-negotiate "1.0"]
		 [org.seleniumhq.selenium.client-drivers/selenium-java-client-driver "1.0.2"]]
  :dev-dependencies [ [lein-javac "1.2.1-SNAPSHOT"] [lein-eclipse "1.0.0"] ])
