(defmigration create-{{plural}}
  (up []
      (create
       (table :{{plural}}
              (integer :id :auto-inc :primary-key){{#fields}}
              {{sql-column}}{{/fields}}
              (timestamp :created-on (default (now)))))))
