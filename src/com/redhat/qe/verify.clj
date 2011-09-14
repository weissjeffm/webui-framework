(ns com.redhat.qe.verify
  (:use [clojure.string :only [split]])
  (:import [java.util.logging Logger Level]
           [com.redhat.qe.auto.testng LogMessageUtil$Style]))

(defn ^{:private true} local-bindings
  "Produces a map of the names of local bindings to their values."
  [env]
  (let [symbols (keys env)]
    (zipmap (for [sym symbols] `(quote ~sym)) symbols)))

(defn symbols [sexp]
  "Returns just the symbols from the expression, including those
   inside literals (sets, maps, lists, vectors)."
  (distinct (filter symbol? (tree-seq coll? seq sexp))))

(defn used-bindings [m form]
  (select-keys m (symbols form)))

(defmacro verify
  "Evaluates expr and either logs what was evaluated, or throws an exception if it does not evaluate to logical true."
  [x]
  (let [bindings (local-bindings &env)]
    `(let [noerr# (atom true)
           res# (try ~x (catch Exception e# (do (reset! noerr# false) e#)))
           sep#  (System/getProperty "line.separator")
           form# '~x
           loc# (-> (Thread/currentThread) .getStackTrace second .getClassName (split #"\$"))
           clazz# (str *ns*)
           msg# (apply str (if (and @noerr# res#) "Verified: " "Verification failed: ") (pr-str form#) sep# (for [[k# v#] (used-bindings ~bindings form#)]
                         (str "\t" k# " : " v# sep#)))]
       (if (and @noerr# res#) (.logp (Logger/getLogger (first loc#))
                                    (Level/INFO)
                                    (first loc#)
                                    (second loc#)
                                    msg#
                                    (LogMessageUtil$Style/Asserted))
           (let [err# (AssertionError. msg#)]
             (throw (if (and res# (not @noerr#))
                      (.initCause err# res#)
                      err#)))))))

(defn check [tval form bindings err]
  (if (not tval)
    (let [sep (System/getProperty "line.separator")
          msg (apply str "Verification failed: "
                     (pr-str form) sep
                     (for [[k v] bindings] (str "\t" k " : " (pr-str v) sep)))
          e (AssertionError. msg)]
      (when err (.initCause e err))
      (throw e))
    tval))

(defmacro verify-that [x]
  (let [bindings (local-bindings &env)]
    `(let [err# (atom nil)
           res# (try ~x (catch Exception e# (do (reset! err# e#) nil)))
           form# '~x]
       (check res# form# (used-bindings ~bindings form#) @err#))))
