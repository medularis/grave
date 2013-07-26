(ns leiningen.new.grave-gen
  (:use clojure.java.io
        [clojure.string :only (split)]
        [leiningen.new.templates :only [*dir* renderer sanitize ->files render-text]]))

(def type->input
  {"boolean" "check-box"
   "text"    "text-area"})

(defn type->sql-column
  [name type]
  (str
   "("
   (case type
     "string"    "varchar"
     nil         "varchar"
     "text"      "text"
     "boolean"   "boolean"
     "int"       "integer"
     "date"      "date"
     "time"      "time"
     "date-time" "timestamp")
   " :" name
   (case type
     "string" " 255)"
     nil      " 255)"
     ")")))

(def render (renderer "grave_gen"))

(defn grave-gen
  [{:keys [name]} [scaffold plural singular & fields]]
  (binding [*dir* "."]
    (let [fields  (map
                   (fn [field-type]
                     (let [[field type] (split field-type #":")]
                       {:name        field
                        :type        type
                        :sql-column  (type->sql-column field type)
                        :input       (type->input type "text-field")
                        :not-string? (and type (not= type "text") (not= type "string"))}))
                   fields)
          data    {:name             name
                   :project          name
                   :plural           plural
                   :sanitized        (sanitize name)
                   :sanitized-plural (sanitize plural)
                   :singular         singular
                   :fields           fields}]

      (when (= scaffold "sql-scaffold")
        (let [migration         (str "create_" plural ".clj")
              lobos-config      "./src/lobos/config.clj"
              migrations        "./src/lobos/migrations.clj"
              require-migration (str "\n\n(load-file \"resources/migrations/create_" plural ".clj\")")]

          (when-not (.exists (file lobos-config))
            (println (str "Generating lobos configuration file"))
            (->files data [lobos-config (render "lobos_config.clj" data)]))

          (if (.exists (file migrations))
            (do
              (println (str "Appending new migration to migrations namespace"))
              (spit migrations require-migration :append true))
            (do
              (println (str "Generating lobos configuration file"))
              (->files data [migrations (str (render "migrations.clj" data)
                                             require-migration)])))

          (println (str "Generating migration " migration))
          (->files
           data
           [(str "resources/migrations/create_" plural ".clj")
            (render "migration.clj" (assoc data :migration (str "create-" plural)))]))

        (let [entities-path (str "./src/" (sanitize name) "/models/entities.clj")
              new-entity    (str "\n\n(defentity " plural ")")]
          (if (.exists (file entities-path))
            (do
              (println (str "Appending korma entity " plural " to src/models/entities.clj."))
              (spit entities-path new-entity :append true))
            (do
              (println (str "Generating korma entities file in src/models/entities.clj."))
              (->files
               data
               ["src/{{sanitized}}/models/entities.clj"
                (str (render "entities.clj" data)
                     new-entity)])))))

      (println (str "Generating handler, view and model for " plural "."))

      (->files
       data
       ["src/{{sanitized}}/handlers/{{sanitized-plural}}.clj"
        (render "handlers.clj" data)]
       ["src/{{sanitized}}/views/{{sanitized-plural}}.clj"
        (render "views.clj" data)]
       (if (= scaffold "sql-scaffold")
         ["src/{{sanitized}}/models/{{sanitized-plural}}.clj"
          (render "korma-model.clj" data)]
         ["src/{{sanitized}}/models/{{sanitized-plural}}.clj"
          (render "model.clj" data)]))
      (println (str "Files for " plural " generated successfully.")))))
