(ns leiningen.grave
  (:use clojure.java.io
        [clojure.string :only (split)]
        stencil.core))

(def type->input
  {"boolean" "check-box"
   "text"    "text-area"})

(defn ->files
  [data & files]
  (doseq [[path source] files]
    (let [path (render-string path data)]
      (.mkdirs (.getParentFile (file path)))
      (copy source (file path)))))

(defn gen
  [project [engine plural singular & fields]]
  (let [fields (map
                (fn [field-type]
                  (let [[field type] (split field-type #":")]
                    {:name    field
                     :type    type
                     :input   (type->input type "text-field")
                     :not-string? (and type (not= type "text"))}))
                fields)
        data   {:project  (:name project)
                :plural   plural
                :singular singular
                :fields   fields}]
    (if (= engine "korma")
      (let [entities-path (render-string "{{project}}/models/entities.clj" data)
            new-entity    (render-string "\n\n(defentity {{plural}})" data)]
        (if (.exists (file entities-path))
          (spit entities-path new-entity :append true)
          (->files
           data
           ["{{project}}/models/entities.clj"
            (str (render-file "templates/entities.clj" data)
                 new-entity)]))))
    (->files
     data
     ["{{project}}/handlers/{{plural}}.clj"
      (render-file "templates/handlers.clj" data)]
     ["{{project}}/views/{{plural}}.clj"
      (render-file "templates/views.clj" data)]
     (case engine
       "korma"
       ["{{project}}/models/{{plural}}.clj"
        (render-file "templates/korma-model.clj" data)]
       ["{{project}}/models/{{plural}}.clj"
        (render-file "templates/model.clj" data)]))))

(defn grave
  [project cmd & args]
  (case cmd
    "gen" (gen project args)))
