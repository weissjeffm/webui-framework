(ns com.redhat.qe.auto.selenium.selenium
  (:import [com.redhat.qe.auto.selenium ExtendedSelenium Element LocatorTemplate]))

(defprotocol SeleniumLocatable
  (sel-locator [x]))

(def sel (atom nil))

(defn connect "Create a new selenium instance." [host port browser-type url]
  (reset! sel (ExtendedSelenium. host port browser-type url)))

(defn new-element [locator-strategy & args]
  (Element. locator-strategy (into-array args)))

(defn locator-args
  "If any args are keywords, look them up via
SeleniumLocatable protocol (which should return a selenium String
locator). Returns the args list with those Strings in place of the
keywords."
  [& args]
  (for [arg args]
    (if (keyword? arg) 
      (or (sel-locator arg)
          (throw (IllegalArgumentException.
                  (str "Locator " arg " not found in UI mapping."))))
      arg)))

(defn call-sel [action & args]
  (clojure.lang.Reflector/invokeInstanceMethod
   (deref sel) action (into-array Object (apply locator-args args))))

(defmacro browser
  "Call method 'action' on selenium, with the given args - keywords
will be looked up and converted to String locators (see locator-args)"
  [action & args]
  `(call-sel ~(str action) ~@args))

(defmacro ->browser "Performs a series of actions using the browser"
  [ & forms]
  `(do ~@(for [form forms] `(browser ~@form))))

(defn fill-form
  "Fills in a standard HTML form.  items-map is a
mapping of locators of form elements, to the string values that should
be selected or entered.  'submit' is a locator for the submit button
to click at the end."
  [items-map submit]
  (doseq [[el val] items-map]
    (if val
      (if (= "selectlist" (browser getElementType el))
       (browser select el val)
       (browser setText el val))))
  (browser clickAndWait submit))

(defmacro loop-with-timeout [timeout bindings & forms]
  `(let [starttime# (System/currentTimeMillis)]
     (loop ~bindings
       (if  (> (- (System/currentTimeMillis) starttime#) ~timeout)
	 (throw (RuntimeException. (str "Hit timeout of " ~timeout "ms.")))
	 (do ~@forms)))))

(defn first-present [timeout & elements]
  (loop-with-timeout timeout []
    (or (some #(if (browser isElementPresent %1) %1) elements)
        (do (Thread/sleep 1000)
            (recur)))))
