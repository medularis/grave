(ns leiningen.new.grave-gen
  (:use clojure.java.io
        [clojure.string :only (split)]
        [leiningen.new.templates :only [*dir* renderer sanitize ->files]]))

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
                             {:name    field
                              :type    type
                              :input   (type->input type "text-field")
                              :not-string? (and type (not= type "text"))}))
                         fields)
        data            {:name             name
                         :project          name
                         :plural           plural
                         :sanitized        (sanitize name)
                         :sanitized-plural (sanitize plural)
                         :singular         singular
                         :fields           fields}]
    (if (= engine "korma")
      (let [entities-path (str (sanitize name) "/models/entities.clj")
            new-entity (str "\n\n(defentity " plural ")")]
        (if (.exists (file entities-path))
          (spit entities-path new-entity :append true)
          (binding [*dir* "."]
            (->files
             data
             ["src/{{sanitized}}/models/entities.clj"
              (str (render "entities.clj" data)
                   new-entity)])))))
    (binding [*dir* "."]
      (->files
       data
       ["src/{{sanitized}}/handlers/{{sanitized-plural}}.clj"
        (render "handlers.clj" data)]
       ["src/{{sanitized}}/views/{{sanitized-plural}}.clj"
        (render "views.clj" data)]
       (case engine
         "korma"
         ["src/{{sanitized}}/models/{{sanitized-plural}}.clj"
          (render "korma-model.clj" data)]
         ["src/{{sanitized}}/models/{{sanitized-plural}}.clj"
          (render "model.clj" data)])))))
