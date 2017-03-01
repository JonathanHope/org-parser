(ns org-parser.parser
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]))

(def org-parser (insta/parser (io/resource "org.bnf")))

(defn parse-org
  "Parse an org document"
  [org-doc]
  (->> (org-parser org-doc)
       (insta/transform
        {:headline-title (fn [& args] [:headline-title (apply str args)])})))
