(ns kalpana.navigation
  (:require [clojure.zip :as zip]
            [clojure.set])
  (:import [java.util NoSuchElementException]))

(defmacro page [name-kw fndef & links]
  (let [m {:page name-kw         
              :fn fndef}
        fn-args (second fndef)
        args  (if (empty? fn-args) {} {:req-args (vec (map keyword fn-args))})
        linkmap (if (nil? links) {} {:links (vec links)})]
    (merge m args linkmap)))

(def page-zip (zip/zipper #(contains? % :links)
                          #(:links %)
                          #(conj %1 {:links %2})
                          nav-tree))

(defn find-node [z pred]
  (->> (iterate zip/next z)
       (take-while #(not (zip/end? %)))
       (filter #(pred (zip/node %)))
       first))

(defn page-path [page z]
  (let [first-match (find-node z #(= (:page %) page))]
    (if (nil? first-match)
      (throw (NoSuchElementException. (str "Page " page " was not found in navigation tree."))))
    (conj (zip/path first-match) (zip/node first-match))))

(defn navigate [page z args]
  (let [path (page-path page z)
        all-req-args (set (mapcat :req-args path))
        missing-args (clojure.set/difference all-req-args (set (keys args)))]
    (if-not (zero? (count missing-args))
      (throw (IllegalArgumentException. (str "Missing required keys to navigate to " page " - " missing-args)))
      (for [step path]
        (apply (:fn step)
               (for [req-arg (:req-args step)]
                 (req-arg args)))))))
