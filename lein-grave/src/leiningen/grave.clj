(ns leiningen.grave
  (:use leiningen.new.grave-gen))

(defn grave
  [project cmd & args]
  (case cmd
    "gen" (grave-gen project args)))
