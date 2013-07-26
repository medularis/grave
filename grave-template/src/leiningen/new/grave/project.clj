(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [sebastiansen/grave "0.1.1-SNAPSHOT"]

                 ;; Database
                 [postgresql "9.1-901.jdbc4"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [org.apache.derby/derby "10.8.1.2"]
                 [hsqldb "1.8.0.10"]
                 [postgresql "8.4-702.jdbc4"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [com.novemberain/monger "1.6.0"]
                 ]
  :plugins [[lein-ring "0.8.6"]
            [lein-grave "0.1.0"]
            [configleaf "0.4.6"]]
  :hooks [configleaf.hooks]
  :ring {:handler {{name}}.handler/app}
  :db :dev
  :profiles {:dev {:dependencies [[ring-mock "0.1.5"]]
                   :db           :dev}})
