(ns grave.core
  (:use rip.core
        grave.util
        compojure.handler
        taoensso.tower.ring
        ring.middleware.anti-forgery))

(defn handlers-ns
  []
  (use 'rip.core
       'rip.validation
       'rip.util
       'grave.handlers
       'grave.validation
       'grave.util
       '[taoensso.tower :only (t)]
       '[ring.util.response :exclude (response)])
  (require '[noir.response :as response]))

(defn views-ns
  [& [{:keys [form-engine]}]]
  (use '[rip.core :only (path-for link-for url-for)]
       '[taoensso.tower :only (t)]
       'grave.views
       'grave.util
       'hiccup.page
       'hiccup.element)
  (use 'clj-simple-form.giddyup
       'clj-simple-form.input
       'clj-simple-form.fields
       '[hiccup.form :only (submit-button)]
       '[clj-simple-form.form-scope :only (value-for error-for)]))

(defn grave-handler
  "Create a handler for your app, passing a list of RIP's routes and the following optional middleware:
  - i18n
  - anti-forgery
It includes compojure's site handler usign the same options map, see http://weavejester.github.io/compojure/compojure.handler.html."
  [routes & [{:keys [middleware i18n anti-forgery?] :as opts}]]
  (-> (apply routes-for routes)
      (wrap-if (:enabled i18n) wrap-i18n-middleware)
      (wrap-if anti-forgery? wrap-anti-forgery)
      (wrap-if middleware middleware)
      (site opts)))
