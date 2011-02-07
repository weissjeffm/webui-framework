(ns com.redhat.qe.auto.selenium.selenium
  (:import [com.redhat.qe.auto.selenium ExtendedSelenium]))

(defprotocol SeleniumLocatable
  (sel-locator [x]))

(def sel (atom nil))

(defn connect [host port browser-type url]
  (reset! sel (ExtendedSelenium. host port browser-type url)))

(defmacro browser [action & args]
  `(let [locator-args# (for [arg# [~@args]]
                       (if (keyword? arg#) (sel-locator arg#) arg#))]
    (clojure.lang.Reflector/invokeInstanceMethod
     (deref sel) ~(str action) (into-array locator-args#))))
