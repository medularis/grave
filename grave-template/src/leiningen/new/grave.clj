(ns leiningen.new.grave
  (:use [leiningen.new.templates :only [renderer name-to-path ->files]]))

(def render (renderer "grave"))

(defn grave
  [name]
  (let [render (renderer "grave")
        data {:name name
              :title (.toUpperCase name)
              :sanitized (name-to-path name)}]
    (println (str "Generating a grave application called " name "."))
    (->files data
             ;; main
             ["README.md" (render "README.md" data)]
             ["project.clj" (render "project.clj" data)]
             [".gitignore" (render ".gitignore" data)]
             ["src/{{sanitized}}/handler.clj" (render "handler.clj" data)]
             ["src/{{sanitized}}/config.clj" (render "config.clj" data)]
             ["src/{{sanitized}}/globals.clj" (render "globals.clj" data)]
             ["resources/database.edn" (render "database.edn" data)]
             "resources/public"
             ;; i18n
             ["src/{{sanitized}}/i18n.clj" (render "i18n.clj" data)]
             ["src/{{sanitized}}/i18n/en.clj" (render "en.clj" data)]
             ;; layouts
             ["src/{{sanitized}}/views/layouts.clj" (render "layouts.clj" data)]
             ;; welcome
             ["src/{{sanitized}}/handlers/welcome.clj" (render "welcome_handler.clj" data)]
             ["src/{{sanitized}}/views/welcome.clj" (render "welcome_view.clj" data)]
             ;; test
             ["test/{{sanitized}}/test/handler.clj" (render "handler_test.clj" data)])))
