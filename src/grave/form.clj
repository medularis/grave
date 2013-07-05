(ns grave.form
  (:use rip.core
        hiccup.def
        hiccup.form
        [hiccup.core :exclude (h)]
        [clj-simple-form.form-scope :only (value-for *form-values*)]))

(defmacro with-many-nested-form-scope
  [coll item partial]
  (let [resolved (ns-resolve *ns* 'with-nested-form-scope)]
    `(~resolved
      ~coll
      (doall
       (map-indexed
        (fn [i# value#]
          (let [x# (keyword (str i#))]
            (~resolved x#
                       (~resolved ~item
                                  (~partial)))))
        *form-values*)))))

(defmacro make-partial
  [resolved coll item partial]
  `(~resolved ~coll
              (~resolved :new-item
                         (~resolved ~item (~partial)))))

(defmacro add-assoc
  [coll item partial class & content]
  (let [resolved (ns-resolve *ns* 'with-nested-form-scope)]
    `(do
       [:a
        {:class         (str ~class " add-assoc")
         :href          "#"
         :data-partial  (html (make-partial ~resolved ~coll ~item ~partial))
         :data-selector (keyword (str "#" (name ~coll)))}
        ~@content])))

(defelem remove-assoc
  [class & content]
  (let [exists? (boolean (value-for :id))]
    (html
     (hidden-field :_destroy)
     [:a
      {:href "#"
       :class (str class " remove-assoc " (if exists? "exists" "dynamic"))}
      content])))
