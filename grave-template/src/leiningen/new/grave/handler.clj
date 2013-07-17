(ns {{name}}.handler
    (:use grave.core
          compojure.route
          compojure.core
          {{name}}.i18n
          [{{name}}.config :only (config)]
          [{{name}}.handlers.welcome :only (welcome)]))

(defroutes app
  (grave-handler
   [
    ;; Add your defscope, defresources or defroute definitions here
    welcome
    ]
   config)
  (resources "/")
  (not-found "Not Found"))
