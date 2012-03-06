(ns com.redhat.qe.config
  (:use [clojure.string :only [split join]]))

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

(defn kw-to-text
  "Convert a keyword to plain text, eg, :hi-there 'Hi There'. Passes
   each word through word-fn, and the entire text through val-fn."
  ([kw] (kw-to-text kw identity identity))
  ([kw word-fn] (kw-to-text kw word-fn identity))
  ([kw word-fn val-fn] (->> (-> kw name (split #"-"))
                          (map word-fn) (join " ") val-fn)))

(defn same-name "takes a collection of keywords like :registration-settings
and returns a mapping like :registration-settings -> 'Registration Settings'" 
  ([word-fn val-fn coll]
     (zipmap coll (for [kw coll] (kw-to-text kw word-fn val-fn)))))
