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
       '[taoensso.tower :only (t)]))

(defn views-ns
  [& [{:keys [form-engine]}]]
  (use '[rip.core :only (path-for link-for url-for)]
       '[taoensso.tower :only (t)]
       'grave.views
       'grave.util
       'hiccup.page
       'hiccup.element)
  (case form-engine
    :simple-form
    (use 'clj-simple-form.form-scope
         'clj-simple-form.input
         'clj-simple-form.fields)
    :simple-form-giddyup
    (use 'clj-simple-form.giddyup
         'clj-simple-form.input
         'clj-simple-form.fields
         '[clj-simple-form.form-scope :only (value-for error-for)])
    (use 'hiccup.form
         'clj-simple-form.form-scope
         'clj-simple-form.input)))

(defn grave-handler
  "Create a handler for your app, passing a list of RIP's routes and the following optional middleware:
  - i18n
  - anti-forgery
It includes compojure's site handler usign the same options map, see http://weavejester.github.io/compojure/compojure.handler.html."
  [routes & [{:keys [middleware i18n? anti-forgery?] :as opts}]]
  (-> (apply routes-for routes)
      (wrap-if i18n? wrap-i18n-middleware)
      (wrap-if anti-forgery? wrap-anti-forgery)
      (wrap-if middleware middleware)
      (site opts)))
