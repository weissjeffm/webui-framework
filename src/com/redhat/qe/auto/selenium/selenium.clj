(ns com.redhat.qe.auto.selenium.selenium
  (:import [com.redhat.qe.auto.selenium ExtendedSelenium Element LocatorTemplate]))

(defprotocol SeleniumLocatable
  (sel-locator [x]))

(def sel (atom nil))


(defn connect [host port browser-type url]
  (reset! sel (ExtendedSelenium. host port browser-type url)))

(defn new-element [locator-strategy & args]
  (Element. locator-strategy (into-array args)))

(defmacro browser [action & args]
  `(let [locator-args# (for [arg# [~@args]]
                         (if (keyword? arg#)
                           (let [locator# (sel-locator arg#)]
                             (if (nil? locator#)
                               (throw (IllegalArgumentException.
                                       (str "Locator " arg# " not found in UI mapping.")))
                               locator#))
                           arg#))]
    (clojure.lang.Reflector/invokeInstanceMethod
     (deref sel) ~(str action) (into-array Object locator-args#))))
