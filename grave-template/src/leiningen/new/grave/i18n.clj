(ns {{name}}.i18n
    (:use [{{name}}.i18n.en :only (en)])
    (:require [taoensso.tower :as tower]))

(tower/merge-config!
 {:dictionary
  {:en en}})
