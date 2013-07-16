(ns {{project}}.handlers.{{plural}}
  (:use grave.core)
  (:require [my-app.views.{{plural}} :as view]
            [my-app.views.layouts :as layouts]
            [my-app.model :as model]))

(handlers-ns)

(defvalidator {{singular}}-validator
  {{#fields}}
  (field :{{name}} {{#not-string?}} (parse-to :{{type}}) {{/not-string?}})
  {{/fields}})

(defresources {{plural}}
  (with-layout layouts/default)

  (with-find-resource :{{singular}}*
    (h [id] (model/find-one {:id id}))
    {:actions [:show :edit :make :change :destroy]})

  (index
   [page per_page]
   (respond-to
    :html (view/index (model/all page per_page) page per_page)
    :json (render-json (model/all page per_page))))

  (new*
   []
   (respond-to
    :html (view/new* (model/build))
    :json (render-json (model/build))))

  (show
   [{{singular}}*]
   (respond-to
    :html (view/show {{singular}}*)
    :json (render-json {{singular}}*)))

  (make
   [{{singular}}]
   (if-valid
    ({{singular}}-validator {{singular}}) [{{singular}} errors]
    (let [{id :id} (model/create {{singular}})
          location (path-for :{{plural}} [:show] id)]
      (respond-to
       :html (-> (redirect-after-post location)
                 (flash (t :messages.{{plural}}/created)))
       :json (-> (render-json {{singular}})
                 (created location))))
    (respond-to
     :html (view/new* {{singular}} errors)
     :json (-> (render-json errors) unprocessable-entity))))

  (edit [{{singular}}*] (view/edit {{singular}}*))

  (change
   [id {{singular}}]
   (if-valid
    ({{singular}}-validator {{singular}}) [{{singular}} errors]
    (do
      (model/update-fields id {{singular}})
      (respond-to
       :html (-> (redirect (path-for :{{plural}} [:show] (:id {{singular}})))
                 (flash (t :messages.{{plural}}/updated)))
       :json no-content))
    (respond-to
     :html (view/edit {{singular}} errors)
     :json (-> (render-json errors) unprocessable-entity))))

  (destroy
   [id]
   (model/destroy id)
   (respond-to
    :html (-> (redirect (path-for :{{plural}} [:index]))
              (flash (t :messages.{{plural}}/deleted)))
    :json no-content)))
