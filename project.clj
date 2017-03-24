(defproject org-parser "0.1.0-SNAPSHOT"
  :description "Parses an org file into a hiccup tree."
  :url "https://github.com/JonathanHope/org-parser"

  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [instaparse "1.4.5"]
                 [com.rpl/specter "1.0.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [midje "1.8.3"]])
