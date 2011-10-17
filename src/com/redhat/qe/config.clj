(ns com.redhat.qe.config
  (:use [clojure.string :only [split join]])
  (:import [com.redhat.qe.auto.testng TestScript]))

(defn property-map
  "Takes a map as an argument, and produces a new map, where the
   values of the map are looked up as java system properties.  The
   values in the map passed in should be lists, where the first item
   is the system property to look up, and option second item is a
   default value."
  [map]
  (zipmap (keys map)
	  (for [[k d] (vals map)]
            (System/getProperty k d))))

(defn same-name "takes a collection of keywords like :registration-settings
and returns a mapping like :registration-settings -> 'Registration Settings'" 
  ([coll] (same-name identity identity coll))
  ([word-fn coll] (same-name word-fn identity coll))
  ([word-fn val-fn coll]
     (zipmap coll (for [keyword coll]
                    (->>
                     (-> keyword name (split #"-"))
                     (map word-fn) (join " ") val-fn)))))
