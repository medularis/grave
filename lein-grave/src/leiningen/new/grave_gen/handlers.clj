(ns {{project}}.handlers.{{plural}}
  (:use grave.core)
  (:require [{{project}}.views.{{plural}} :as view]
            [{{project}}.views.layouts :as layouts]
            [{{project}}.models.{{plural}} :as model]))

(handlers-ns)

(defvalidator {{singular}}-validator{{#fields}}
  (field :{{name}}{{#not-string?}} (parse-to :{{type}}){{/not-string?}}){{/fields}})

(defresources {{plural}}
  (with-layout layouts/default)

  (with-find-resource :{{singular}}*
    (h [id] (model/find-one {:id id}))
    {:actions [:show :edit :make :change :destroy]})

  (index
   [page per_page]
   (dispatch
    :html (view/index (model/all page per_page) page per_page)
    :json (response/json (model/all page per_page))))

  (new*
   []
   (dispatch
    :html (view/new* (model/build))
    :json (response/json (model/build))))

  (show
   [{{singular}}*]
   (dispatch
    :html (view/show {{singular}}*)
    :json (response/json {{singular}}*)))

  (make
   [{{singular}}]
   (if-valid
    ({{singular}}-validator {{singular}}) [{{singular}} errors]
    (let [{id :id} (model/create {{singular}})
          location (path-for :{{plural}} [:show] id)]
      (dispatch
       :html (-> (redirect-after-post location)
                 (flash (t :messages.{{plural}}/created)))
       :json (->> (response/json {{singular}})
                  (created location))))
    (dispatch
     :html (view/new* {{singular}} errors)
     :json (unprocessable-entity (response/json errors)))))

  (edit [{{singular}}*] (view/edit {{singular}}*))

  (change
   [id {{singular}}]
   (if-valid
    ({{singular}}-validator {{singular}}) [{{singular}} errors]
    (do
      (model/update-fields id {{singular}})
      (dispatch
       :html (-> (redirect (path-for :{{plural}} [:show] (:id {{singular}})))
                 (flash (t :messages.{{plural}}/updated)))
       :json response/empty))
    (dispatch
     :html (view/edit {{singular}} errors)
     :json (unprocessable-entity (response/json errors)))))

  (destroy
   [id]
   (model/destroy id)
   (dispatch
    :html (-> (redirect (path-for :{{plural}} [:index]))
              (flash (t :messages.{{plural}}/deleted)))
    :json response/empty)))
