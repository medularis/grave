(ns {{name}}.views.layouts
    (:use grave.core))

(views-ns)

(defn default
  [& content]
  (html5
   [:head
    [:title "{{title}}"]
    (meta-tags)
    (include-css
     ;; include yout css files here
     )]
   [:body
    content
    (include-js
     ;; include your js files here
     )]))
