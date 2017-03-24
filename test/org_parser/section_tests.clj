(ns org-parser.section-tests
  (:use midje.sweet)
  (:require [org-parser.parser :as parser]))

(facts "about sections"
       (fact "a document can be just a section"
             (parser/parse-org "section") =>
             [:document
              [:section [:paragraph "section"]]])

       (fact "a section can belong to a headline"
             (parser/parse-org "* Headline\nsection") =>
             [:document
              [:headline [:headline-identifier "*"] [:headline-title "Headline"]
               [:section
                [:paragraph "section"]]]])

       (fact "a section can be between headlines but should be nested in the headline above it"
             (parser/parse-org "* Headline\nsection\n* Headline 2") =>
             [:document
              [:headline [:headline-identifier "*"] [:headline-title "Headline"]
               [:section
                [:paragraph "section"]]]
              [:headline [:headline-identifier "*"] [:headline-title "Headline 2"]]]))
