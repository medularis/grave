(ns {{project}}.views.{{plural}}
    (:use grave.core))

(views-ns {:form-engine :simple-form-giddyup})

(defview index
  [{{plural}}]
  [:h2 "{{plural}}"]
  [:table
   [:thead
    [:tr{{#fields}}
     [:th (t [:{{plural}}.fields/{{name}} "{{name}}"])]{{/fields}}
     [:th (t [:{{plural}}.actions/show "show"])]]]
   [:tbody
    (for [{{singular}} {{plural}}]
      [:tr{{#fields}}
       [:td (:{{name}} {{singular}})]{{/fields}}
       [:td  (link-to (path-for :{{plural}} [:show] (:id {{singular}}))
                      (t [:buttons/show "show"]))]])]]
  [:br]
  (link-to (path-for :{{plural}} [:new]) (t [:buttons/show "new"])))

(defview show
  [{{singular}}]
  [:h2 "{{singular}}"]
  [:dl{{#fields}}
   [:dt (t [:{{plural}}.fields/{{name}} "{{name}}"])]
   [:dd (:{{name}} {{singular}})]{{/fields}}]
  (link-to (path-for :{{plural}} [:edit] (:id {{singular}}))
           (t [:{{plural}}.actions/edit "edit"])))

(defn form
  [{{singular}} errors action]
  (form-for
   :{{singular}} action
   {:value {{singular}} :errors errors}{{#fields}}
   ({{input}} :{{name}}){{/fields}}
   [:input {:type "submit"}]))

(defview new*
  [{{singular}} & [errors]]
  [:h2 (t [:{{plural}}/new "new {{singular}}"])]
  (form {{singular}} errors (action-for :{{plural}} [:make])))

(defview edit
  [{{singular}} & [errors]]
  [:h2 (t [:{{plural}}/edit "edit {{singular}}"])]
  (form {{singular}} errors (action-for :{{plural}} [:change] (:id {{singular}}))))
