(ns grave.core
  (:use rip.core
        grave.util
        compojure.handler
        taoensso.tower.ring
        ring.middleware.anti-forgery))

(defn grave-handler
  "Create a handler for your app, passing a list of RIP's routes and the following optional middleware:
  - i18n
  - anti-forgery
It includes compojure's site handler usign the same options map, see http://weavejester.github.io/compojure/compojure.handler.html."
  [routes {:keys [i18n? anti-forgery?] :as opts}]
  (-> (apply routes-for routes)
      (if-> i18n? wrap-i18n-middleware)
      (if-> anti-forgery? wrap-anti-forgery)
      (site opts)))
