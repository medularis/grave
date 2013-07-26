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

  (with-parse-route-params {:id :int})

  (with-find-resource
    (h [id] (model/find-one id))
    :{{singular}}*
    {:actions [:show :edit :change :destroy]})

  (index
   [page per_page]
   (dispatch
    :html (view/index (model/all page per_page))
    :json (response/json (model/all page per_page))))

  (show
   [{{singular}}*]
   (dispatch
    :html (view/show {{singular}}*)
    :json (response/json {{singular}}*)))

  (new*
   []
   (dispatch
    :html (view/new* (model/build))
    :json (response/json (model/build))))

  (make
   [{{singular}}]
   (if-valid
    (validate {{singular}}-validator {{singular}}) [{{singular}} errors]
    (let [user     (model/create {{singular}})
          location (path-for :{{plural}} [:show] (:id user))]
      (dispatch
       :html (->> (redirect-after-post location)
                  (flash (t [:{{plural}}.messages/created "{{singular}} created successfully."])))
       :json (->> (response/json {{singular}})
                  (created location))))
    (dispatch
     :html (view/new* {{singular}} errors)
     :json (unprocessable-entity (response/json errors)))))

  (edit [{{singular}}*] (view/edit {{singular}}*))

  (change
   [id {{singular}}]
   (if-valid
    (validate {{singular}}-validator {{singular}}) [{{singular}} errors]
    (do
      (model/update-fields id {{singular}})
      (dispatch
       :html (->> (redirect (path-for :{{plural}} [:show] id))
                  (flash (t [:{{plural}}.messages/updated "{{singular}} updated successfully."])))
       :json response/empty))
    (dispatch
     :html (view/edit {{singular}} errors)
     :json (unprocessable-entity (response/json errors)))))

  (destroy
   [id]
   (model/destroy id)
   (dispatch
    :html (-> (redirect (path-for :{{plural}} [:index]))
              (flash (t [:{{plural}}.messages/deleted "{{singular}} deleted successfully."])))
    :json response/empty)))
