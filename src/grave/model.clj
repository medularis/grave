(ns grave.model
  (:use korma.core))

(defn new-record?
  [record]
  (or (nil? (:id record))
      (and (string? (:id record))
           (empty? (:id record)))))

(defn unique?*
  [ent field record]
  (-> (select* ent)
      (limit 1)
      (where (if (:id record)
               (and (= field (field record))
                    (not= :id (:id record)))
               (= field (field record))))))

(defn unique?
  [ent field record]
  (-> (unique?* ent field record)
      exec
      empty?))
