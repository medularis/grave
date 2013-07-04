(ns grave.core
  (:use ring.middleware.anti-forgery
        rip.core
        grave.util
        compojure.handler
        taoensso.tower.ring
        [compojure.core :only (defroutes)]))

(declare ^{:dynamic true} *request*)

(defn url-for
  [route & args]
  (str
   (name (:scheme *request*))
   "://"
   (get-in *request* [:headers "host"])
   (apply path-for route args)))

(defn wrap-request
  [h]
  (fn [r]
    (binding [*request* r]
      (h r))))

(defn app
  "Create a handler for your app, passing a list of RIP's routes and the following optional middleware:
  - i18n
  - anti-forgery
Also a before and after handlers can be passed to be added before and after this options.
It includes compojure's site handler usign the same options map, see http://weavejester.github.io/compojure/compojure.handler.html."
  [routes {:keys [i18n? anti-forgery? after before] :as opts}]
  (-> (apply routes-for routes)
      (if-> after after)
      (if-> i18n? wrap-i18n-middleware)
      (if-> anti-forgery? wrap-anti-forgery)
      (if-> before before)
      wrap-request
      (site opts)))

(defmacro defapp
  [name options & body]
  `(defroutes ~name
     (-> (app options)
         ~@body)))
