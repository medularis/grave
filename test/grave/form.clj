(ns grave.test.form
  (:use grave.form
        clj-simple-form.giddyup
        hiccup.form
        hiccup.core))

(html
 (with-form-scope
   :user {:documents {:0 {:document {:name "hola"}}
                      :1 {:document {:name "hola"}}}}
   {}
   (text-field :name)
   (with-many-nested-form-scope
     :documents
     :document
     (fn [] (text-field :name)))
   (add-assoc :documents :document (fn [] (text-field :name)) "asdasd")))

(html
 (form-for
  :user [:post "/"] {:html {:class "afsdfg"} }
  (text-field :name)))
