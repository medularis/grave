(ns {{project}}.views.{{plural}}
    (:use grave.core))

(views-ns {:form-engine :simple-form-giddyup})

(defview index
  [{{plural}}]
  [:table
   [:thead
    [:tr{{#fields}}
     [:th (t :{{plural}}.fields/{{name}})]{{/fields}}]]
   [:tbody
    (for [{{singular}} {{plural}}]
      [:tr{{#fields}}
       [:td (:{{name}} {{singular}})]{{/fields}}])]])

(defview show
  [{{singular}}]
  [:dl{{#fields}}
   [:dt (t :{{plural}}.fields/{{name}})] [:dd (:{{name}} {{singular}})]{{/fields}}]
  (link-to (path-for :{{plural}} [:edit] (:id {{singular}})) (t :buttons/edit)))

(defn form
  [{{singular}} errors action]
  (form-for
   :{{singular}} action
   {:value {{singular}} :errors errors}{{#fields}}
   ({{input}} :{{name}}){{/fields}}))

(defview new*
  [{{singular}} & [errors]]
  [:h1 (t :{{plural}}/new)]
  (form {{singular}} errors (action-for :{{plural}} [:make])))

(defview edit
  [{{singular}} & [errors]]
  [:h1 (t :{{plural}}/edit)]
  (form {{singular}} errors (action-for :{{plural}} [:change] (:id {{singular}}))))
