(ns com.redhat.qe.config
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

