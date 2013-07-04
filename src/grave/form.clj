(ns grave.form
  (:use rip.core
        hiccup.def
        hiccup.form
        [hiccup.core :exclude (h)]
        [clj-simple-form.form-scope :only (value-for)]))

(defmacro with-many-nested-form-scope
  [coll item partial]
  `(let [resolved# (ns-resolve *ns* 'with-nested-form-scope)]
     (resolved#
      ~coll
      (map-indexed
       (fn [i# value#]
         (let [x# (keyword (str i#))]
           (resolved# x#
                      (resolved# ~item
                                 (~partial)))))
       (value-for ~coll)))))

(defmacro make-partial
  [coll item partial]
  `(let [resolved# (ns-resolve *ns* 'with-nested-form-scope)]
     (->> (resolved# ~item (~partial))
          (resolved# :new-item)
          (resolved# ~coll)
          html)))

(defn add-assoc
  [coll item partial class & content]
  [:a
   {:class         (str class " add-assoc")
    :href          "#"
    :data-partial  (make-partial coll item partial)
    :data-selector (keyword (str "#" (name coll)))}
   content])

(defelem remove-assoc
  [class & content]
  (let [exists? (boolean (value-for :id))]
    (html
     (hidden-field :_destroy)
     [:a
      {:href "#"
       :class (str class " remove-assoc " (if exists? "exists" "dynamic"))}
      content])))
