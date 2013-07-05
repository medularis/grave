(ns grave.test.form
  (:use grave.form
        clj-simple-form.giddyup
        clj-simple-form.fields
        hiccup.core))

(html
 (with-form-scope :user {:documents {:0 {:document {:name "hola"}}
                                     :1 {:document {:name "hola"}}}} {}
   (text-field :name)
   (with-many-nested-form-scope
     :documents
     :document
     (fn [] (text-field :name)))
   (add-assoc :documents :document (fn [] (text-field :name)) "asdasd")
   ))
