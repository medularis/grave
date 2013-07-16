(ns {{project}}.models.{{plural}}
    (:use korma.code
          {{project}}.models.entities))

(defn build [] {})

(defn all
  [& [page per-page]]
  (select {{plural}}
          (limit (per-page))
          (offset (* (dec page) per-page))))

(defn find-one
  [id]
  (first (select {{plural}} (where {:id id}) (limit 1))))

(defn create
  [{{singular}}]
  (let [created (insert {{plural}} (values {{singular}}))]
    (or (:id created) (:GENERATED_KEY created))))

(defn update-fields
  [id fields]
  (update {{plural}} (set-fields fields) (where {:id id})))

(defn destroy
  [id]
  (delete {{plural}} (where {:id id})))
