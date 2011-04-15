(defproject webui-framework "1.0.2-SNAPSHOT"
  :description "A library of automated testing related functions."
  :java-source-path "src"
  :dependencies [[com.google.collections/google-collections "1.0"]
		 [org.testng/testng "[6.0,7.0)"]
		 [com.trilead/trilead-ssh2 "build213-svnkit-1.3-patch"]
                 [commons-logging/commons-logging "1.1.1"]
		 [org.seleniumhq.selenium.client-drivers/selenium-java-client-driver "1.0.2"]
                 [org.uncommons/reportng "1.1.3"]]
  :dev-dependencies [[org.clojure/clojure "1.2.0"]
                     [org.clojure/clojure-contrib "1.2.0"]
                     [swank-clojure "1.2.1"]]
  :javac-options {:debug "on"})
