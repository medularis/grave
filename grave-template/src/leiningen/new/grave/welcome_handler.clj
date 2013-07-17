(ns {{name}}.handlers.welcome
    (:use grave.core)
    (:require [{{name}}.views.welcome :as view]
              [{{name}}.views.layouts :as layouts]))

(handlers-ns)

(defscope welcome
  "/"
  (with-layout layouts/default)
  (index [] (view/index)))
