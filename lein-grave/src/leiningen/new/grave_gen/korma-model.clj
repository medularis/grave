(ns {{project}}.models.{{plural}}
    (:use korma.core
          {{project}}.models.entities))

(def page* 1)

(def per-page* 10)

(defn build
  []
  {})

(defn all
  [& [page per-page]]
  (let [page     (or page page*)
        per-page (or per-page per-page*)]
    (select {{plural}}
            (limit per-page)
            (offset (* (dec page) per-page)))))

(defn find-one
  [id]
  ;; Works only if pk is :id
  (first (select {{plural}} (where {:id id}) (limit 1))))

(defn create
  [{{singular}}]
  ;; Works if id is auto-inc
  (let [created (insert {{plural}} (values {{singular}}))]
    ;; Because of jdbc for Mysql
    (find-one
     (or (:id created) (:GENERATED_KEY created)))))

(defn update-fields
  [id fields]
  ;; Works only if pk is :id
  (update {{plural}} (set-fields fields) (where {:id id})))

(defn destroy
  [id]
  ;; Works only if pk is :id
  (delete {{plural}} (where {:id id})))
