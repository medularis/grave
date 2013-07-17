(ns {{name}}.views.welcome
    (:use grave.core))

(views-ns)

(defview index
  []
  [:h1 "Welcome to {{title}}"])
