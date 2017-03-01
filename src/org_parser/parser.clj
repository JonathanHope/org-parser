(ns org-parser.parser
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]))

(def parse-org (insta/parser (io/resource "org.bnf")))
