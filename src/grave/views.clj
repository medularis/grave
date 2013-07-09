(ns grave.views
  (:use grave.util
        rip.core
        hiccup.def
        hiccup.form
        [hiccup.core :exclude (h)]
        [clj-simple-form.form-scope :only (value-for *form-values*)]
        ring.util.anti-forgery))

(defmacro defview
  [name args & body]
  `(defn ~name
     ~args
     (*layout* ~@body)))

(defmacro form-for
  [name action opts & body]
  (let [resolved (ns-resolve *ns* 'with-form-scope)]
    `(form-to (or (:html ~opts) {})
              ~action
              (if-not (:insecure ~opts) (anti-forgery-field))
              (~resolved ~name
                         (or (:value ~opts) {})
                         (or (:errors ~opts) {})
                         ~@body))))

(defelem form-to+
  [[method action] & body]
  (form-to
   [method action]
   (anti-forgery-field)
   body))

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

(defn with-id-field
  []
  (if (value-for :id)
    (hidden-field :id)))

(defn request-flash
  []
  (:flash *request*))
