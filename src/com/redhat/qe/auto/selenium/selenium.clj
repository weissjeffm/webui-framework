(ns com.redhat.qe.auto.selenium.selenium
  (:import [com.redhat.qe.auto.selenium ExtendedSelenium]))

(def sel (atom nil))

(defn connect [host port browser-type url]
  (reset! sel (ExtendedSelenium. host port browser-type url)))

(defmacro ui [action & args]
  `(clojure.lang.Reflector/invokeInstanceMethod (deref sel) ~(str action) (into-array (quote ~args))))
