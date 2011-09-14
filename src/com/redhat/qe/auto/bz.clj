(ns com.redhat.qe.auto.bz
  (:import [com.redhat.qe.auto.testng BzChecker]))

(def docheck 
  (memoize (fn [checker id] (.isBugOpen checker id))))

(defn open-bz-bugs "Filters bug ids and returns only those that are still open." [ & ids]
  (with-meta (fn [_]
               (let [checker (BzChecker/getInstance)
                     still-open (filter (fn [id] (docheck checker id))
                                        ids)]
                 (if (= 0 (count still-open)) nil
                     still-open)))
    {:type :bz-blocker
     ::source `(~'open-bz-bugs ~@ids)}))

(defmethod print-method :bz-blocker [o ^Writer w]
  (print-method (::source (meta o)) w))
