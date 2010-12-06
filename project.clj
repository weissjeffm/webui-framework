(defproject webui-framework "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :java-source-path "src"
  :java-options {:debug "true" }
  :repositories {"apacherepository" "http://repository.apache.org/snapshots/"}
  :dependencies [[org.apache.xmlrpc/xmlrpc-client "3.1.3"]
		 [org.clojars.kjw/commons-httpclient "3.1"] ]
  :dev-dependencies [ [lein-javac "1.2.1-SNAPSHOT"] ])
