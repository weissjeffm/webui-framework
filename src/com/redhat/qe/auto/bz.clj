(ns com.redhat.qe.auto.bz
  (:import [com.redhat.qe.auto.testng BzChecker]))

(defn blocked-by-bz-bugs [ & ids]
  (with-meta (fn [_]
               (let [checker (BzChecker/getInstance)
                     still-open (filter (memoize (fn [id] (.isBugOpen checker id)))
                                        ids)]
                 (if (= 0 (count still-open)) nil
                     still-open)))
    {:type :bz-blocker
     ::source `(~'blocked-by-bz-bugs ~@ids)}))

(defmethod print-method :bz-blocker [o ^Writer w]
  (print-method (::source (meta o)) w))
