(ns com.redhat.qe.config
  (:use [clojure.contrib.string :only [split join]])
  (:import [com.redhat.qe.auto.testng TestScript]))

(defn property-map "Takes a map as an argument, and produces a new
map, where the values of the map are looked up as java system
properties.  The values in the map passed in should either be a system
property key, or a vector with 2 elements: the key and the default
value if no such system property exists."
  [map]
  (zipmap (keys map)
	  (for [v (vals map)]
            (if (vector? v)
              (System/getProperty (first v) (second v))
              (System/getProperty v)))))

(defn same-name "takes a collection of keywords like :registration-settings
and returns a mapping like :registration-settings -> 'Registration Settings'" 
  ([coll] (same-name identity identity coll))
  ([word-fn coll] (same-name word-fn identity coll))
  ([word-fn val-fn coll]
     (zipmap coll (for [keyword coll]
               (->> keyword name (split #"-") (map word-fn) (join " ") val-fn)))))
