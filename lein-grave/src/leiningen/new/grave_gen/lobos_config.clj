(ns lobos.config
  (:use lobos.connectivity
        {{name}}.config))

(open-global db-spec)
