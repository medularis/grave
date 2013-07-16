(ns leiningen.grave
  (:use clojure.java.io
        [clojure.string :only (split)]
        [leinjacker.eval :only (eval-in-project)]
        [leiningen.new.templates :only (renderer
                                        ->files
                                        sanitize-ns
                                        name-to-path
                                        project-name)]))

(def type->input
  {nil       "text-field"
   "boolean" "check-box"
   "text"    "text-field"})

(defn gen
  [project [engine plural singular & fields]]
  (let [fields (map
                (fn [field-type]
                  (let [[field type] (split field-type #":")]
                    {:name    field
                     :type    type
                     :input   (type->input field)
                     :not-string? (and type (not= type "text"))}))
                fields)
        render (renderer "templates")
        data   {:name     "test"
                :project  (:name project)
                :plural   plural
                :singular singular
                :params   fields}]
    (->files data
     ["{{project}}/handlers/{{plural}}.clj"
      (render "handlers.clj" data)]
     ["{{project}}/views/{{plural}}.clj"
      (render "views.clj" data)]
     (case engine
       "korma"
       ["{{project}}/models/{{plural}}.clj"
        (render "korma-model.clj")]
       ["{{project}}/models/{{plural}}.clj"
        (render "model.clj")]))))

(->files )

(defn grave
  [project cmd & args]
  (case cmd
    "gen" (gen project args)))
