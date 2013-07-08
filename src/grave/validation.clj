(ns grave.validation
  (:use rip.core
        rip.validation
        grave.model
        [taoensso.tower :only (t)])
  (:require [clojure.string :as st]))

(declare ^{:dynamic true} *validation*)

(declare transform-assocs)

(defn- make-path
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

(defn- resolve-field
  [field validator]
  (if field
    (->> (st/split (name field) #"\.")
         (map keyword)
         (make-path validator))
    [:_base]))

(defn form-errors
  [errors validator]
  (reduce
   (fn [errors {:keys [field message]}]
     (assoc-in errors (resolve-field field validator) message))
   {}
   errors))

(defn- compare-nested-values
  [[index1 _] [index2 _]]
  (compare (Long/parseLong index1) (Long/parseLong index2)))

(defn- to-many-array
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

(defn to-form-value
  [validator value]
  (transform-assocs validator value true))

(defn validates-email
  "Validate email format"
  [field]
  (validates
   field
   (fn [email] (boolean (re-matches #".+\@.+\..+" email)))
   (fn [_] (t :errors.messages/email))))

(defn validates-uniqueness-of
  [validator ent field & [error]]
  (validates validator
             (fn [value]
               (unique? ent field value))
             (or error
                 {:message (t :errors.messages/unique)
                  :field   field})))

(defn validates-unique-item
  "Validate if an item is duplicated in a list from a assoc-many relation in a validator"
  [validator rel f error]
  (validates
   validator
   (fn [value]
     (loop [[value & values] (rel value) map {}]
       (let [key (f value)]
         (if (get map key)
           false
           (if values
             (recur values (assoc map key value))
             true)))))
   (or error
       (fn [_]
         {:field   (keyword (str (name rel) "._base"))
          :message (t :errors.messages/unique-item)}))))

(defmacro if-valid-form
  "Use it when a wrap-form-validator is applied.
Bindings: [validated-value error form-value]"
  ([bindings then]
     `(if-valid-form ~bindings ~then nil))
  ([bindings then else & oldform]
     `(let [~bindings [(:value *validation*)
                       (:errors *validation*)
                       (:form-value *validation*)]]
        (if (:valid? *validation*)
          ~then
          ~else))))
