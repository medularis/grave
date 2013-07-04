(ns grave.form)

(declare transform-assocs)

(defn make-path
  ""
  [validator path]
  (loop [[validator rel item-name keys] [validator :one nil []]
         [key & path]         path]
    (if-let [{:keys [validator rel item-name]}
             (get-in validator [:assocs (keyword key)])]
      (if path
        (recur [validator
                rel
                item-name
                (conj keys (keyword key))]
               path)
        (conj keys (keyword key)))
      (if (= rel :many)
        (if path
          (recur [validator
                  :one
                  nil
                  (conj keys (keyword key) (or item-name :item))]
                 path)
          (conj keys (keyword key)))
        (conj keys (keyword key))))))

(defn resolve-field
  [field validator]
  (if field
    (->> (st/split (name field) #"\.")
         (map keyword)
         (make-path validator))
    [:_base]))

(defn form-errors
  [validator errors]
  (reduce
   (fn [errors {:keys [field message]}]
     (assoc-in errors (resolve-field field validator) message))
   {}
   errors))

(defn compare-nested-values
  [[index1 _] [index2 _]]
  (compare (Long/parseLong index1) (Long/parseLong index2)))

(defn to-many-array
  [validator values]
  (reduce
   (fn [vals [_ value]]
     (conj vals (transform-assocs validator (-> value first val))))
   []
   values))

(defn transform-assocs
  [validator value & [after?]]
  (->> (reduce
        (fn [value* [name {:keys [rel validator item-name]}]]
          (if-let [assoc-value (value name)]
            (assoc value*
              name
              (case rel
                :one  (transform-assocs validator assoc-value after?)
                :many (if after?
                        (-> (reduce
                             (fn [[values i] value]
                               [(assoc-in
                                 values
                                 [(keyword (str i))
                                  (or item-name :item)]
                                 value)
                                (inc i)])
                             [{} 0]
                             assoc-value)
                            first)
                        (->> assoc-value
                             (sort compare-nested-values)
                             (to-many-array validator)))))
            value*))
        {}
        (:assocs validator))
       (merge value)))

(defn with-nested-fields
  [coll item partial]
  (with-nested-form-scope coll
    (html5
     (map-indexed
      (fn [i _]
        (let [x (keyword (str i))]
          (with-nested-form-scope x
            (with-nested-form-scope item
              (partial)))))
      *form-values*))))

(defelem add-assoc
  [coll item partial class & content]
  [:a
   {:class         (str class " add-assoc")
    :href          "#"
    :data-partial  (->> (with-nested-form-scope
                          item
                          (partial))
                        (with-nested-form-scope :new-item)
                        (with-nested-form-scope coll)
                        html)
    :data-selector (keyword (str "#" (name coll)))}
   content])

(defelem remove-assoc
  [class & content]
  (let [exists? (boolean (:id *form-values*))]
    (html
     (hidden-field :_destroy)
     [:a
      {:href "#"
       :class (str class " remove-assoc " (if exists? "exists" "dynamic"))}
      content])))
