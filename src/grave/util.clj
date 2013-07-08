(ns grave.util
  (:use rip.core
        rip.util
        rip.validation
        rip.middleware
        ring.util.response
        hiccup.page))

(def ^:dynamic *layout* identity)

(defn wrap-if
  [handler pred wrapper & args]
  (if pred
    (apply wrapper handler args)
    handler))

(defmacro if->
  ""
  [value pred & body]
  `(if-not ~pred
     ~value
     (-> ~value
         ~@body)))

(defn action-for
  [route & args]
  (let [{:keys [href method]} (apply link-for args)]
    [(keyword (.toLowerCase method)) href]))
