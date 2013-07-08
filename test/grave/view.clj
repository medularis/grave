(ns grave.test.view)

;; lein grave generate users user :id string :name string

;; handlers.users

;; (defvalidator user-validator
;;   (field :id)
;;   (field :email validates-email))

;; (defresources users
;;   (with-layout layouts/default)
;;   (with-find-resource :user*
;;     (h [id] (model/find-one id))
;;     {:actions [:show :edit :make :change]})
;;   (with-form-validator user-validator :user
;;     {:actions [:make :change]})
;;   ...)

;; views.users

;; ...

;; entites

;; (defentity users)

;; models.users

;; (defn all
;;   []
;;   (select users))

;; (defn find-one
;;   [id]
;;   (first (select users (where {:id id}) (limit 1))))

;; (defn create
;;   [record]
;;   (insert users (values record)))

;; (defn update-fields
;;   [id fields]
;;   (update users (set-fields fields) (where {:id id})))

;; (defn destroy
;;   [id]
;;   (delete users (where {:id id})))

;; lobos.migrations

;; (defmigration create-users
;;  ...)
