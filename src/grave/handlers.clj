(ns grave.handlers
  (:use rip.core
        grave.util
        rip.util
        rip.validation
        rip.middleware
        ring.util.response
        hiccup.page
        grave.validation)
  (:require [com.twinql.clojure.conneg :as conneg]
            [noir.response :as resp]))

(defmacro edit
  [scope & body]
  `(GET ~scope {:name :edit :path "/:id/edit"} ~@body))

(defmacro new*
  [scope & body]
  `(GET ~scope {:name :new :path "/new"} ~@body))

;; Midleware

(defn with-find-resource
  [scope handler resource-name & [opts]]
  (wrap scope
        (fn [h]
          (fn [r]
            (if-let [resource (handler r)]
              (h (assoc-in r [:params resource-name] resource)))))
        opts))

(defn with-parse-route-params
  [scope route-params & [opts]]
  (wrap scope
        (fn [h]
          (fn [r]
            (h (assoc r :params
                      (reduce
                       (fn [params [param type]]
                         (if-let [value (param params)]
                           (assoc params
                             param
                             ((parsers type) value))
                           params))
                       (:params r)
                       route-params)))))
        opts))

(defn with-layout
  [scope layout & [opts]]
  (wrap scope
        (fn [h] (fn [r] (binding [*layout* layout] (h r))))
        opts))

(defn with-form-validator
  "Wrap a scope with a validation to be used with if-valid-form."
  [scope validator param-key & [opts]]
  (wrap
   scope
   (fn [handler]
     (fn [request]
       (let [value (get-in request [:params param-key])]
         (if value
           (let [value      (transform-assocs validator value)
                 validation (-> (validate validator value)
                                (assoc
                                    :form-value
                                  (transform-assocs validator value true))
                                (update-in [:errors] form-errors validator))]
             (binding [*validation* validation]
               (handler request)))
           (throw (Exception. "Parameter can't be nil"))))))
   opts))

;; Responses

(defmacro dispatch
  [& clauses]
  `(let [ctypes# (set
                  (conneg/best-allowed-content-type
                   (get-in *request* [:headers "accept"] "text/plain")
                   #{"text/html" "application/json" "application/xml"}))]
     (condp (fn [ctype# ctypes#]
              (contains? ctypes# (name ctype#))) ctypes#
       ~@clauses)))

(defn created
  [url & [response]]
  (-> (or response {:body ""})
      (assoc-in [:headers "Location"] url)
      (assoc :status 201)))

(defn flash
  [value & [response]]
  (-> (or response {:status 201 :body ""})
      (assoc :flash value)))

(defn unprocessable-entity
  [& [response]]
  (-> (or response {:body ""})
      (assoc :status 422)))
