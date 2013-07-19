(ns leiningen.new.grave-gen
  (:use clojure.java.io
        [clojure.string :only (split)]
        [leiningen.new.templates :only [*dir* renderer sanitize ->files render-text]]))

(def type->input
  {"boolean" "check-box"
   "text"    "text-area"})

(def render (renderer "grave_gen"))

(defn grave-gen
  [{:keys [name]} [plural singular & [options? & fields]]]
  (let [[fields engine] (if options?
                          (if (.startsWith options? "-")
                            [fields options?]
                            [(cons options? fields)])
                          [])
        fields          (map
                         (fn [field-type]
                           (let [[field type] (split field-type #":")]
                             {:name        field
                              :type        type
                              :input       (type->input type "text-field")
                              :not-string? (and type (not= type "text") (not= type "string"))}))
                         fields)
        data            {:name             name
                         :project          name
                         :plural           plural
                         :sanitized        (sanitize name)
                         :sanitized-plural (sanitize plural)
                         :singular         singular
                         :fields           fields}]

    (if (= engine "-korma")
      (let [entities-path (str "./src/" (sanitize name) "/models/entities.clj")
            new-entity    (str "\n\n(defentity " plural ")")]
        (if (.exists (file entities-path))
          (do
            (println (str "Appending korma entity " plural " to src/models/entities.clj."))
            (spit entities-path new-entity :append true))
          (do
            (println (str "Generating korma entities file in src/models/entities.clj."))
            (binding [*dir* "."]
             (->files
              data
              ["src/{{sanitized}}/models/entities.clj"
               (str (render "entities.clj" data)
                    new-entity)]))))))
    (println (str "Generating handler, view and model for " plural "."))
    (binding [*dir* "."]
      (->files
       data
       ["src/{{sanitized}}/handlers/{{sanitized-plural}}.clj"
        (render "handlers.clj" data)]
       ["src/{{sanitized}}/views/{{sanitized-plural}}.clj"
        (render "views.clj" data)]
       (case engine
         "-korma"
         ["src/{{sanitized}}/models/{{sanitized-plural}}.clj"
          (render "korma-model.clj" data)]
         ["src/{{sanitized}}/models/{{sanitized-plural}}.clj"
          (render "model.clj" data)])))
    (println (str "Files for " plural " generated successfully."))))
