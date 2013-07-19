(ns {{name}}.config
    (:use korma.db
          grave.util
          cfg.current))

(def config
  {
   ;; Add i18n middleware from the tower library in your app
   :i18n?        true

   ;; Add anti-forgery middleware,
   ;; form-for to include the field implicity in your forms
   :anti-forgery true})

(def db-spec
  ;; use korma functions to specify your db specs.
  ;; Examples:
  ;;     (mysql {:user ...})
  ;;     (postgresql {:user ...})
  ;;     (sqlite {:user ...})
  (postgres ((project :db) (load-config "database.edn"))))
