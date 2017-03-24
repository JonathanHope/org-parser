(ns org-parser.header-nesting-tests
  (:use midje.sweet)
  (:require [org-parser.parser :as parser]))

(facts "about headline nesting"
       (fact "lower level headlines are nested properly"
             (parser/parse-org "* Headline\n** Headline 2\n*** Headline 3") =>
             [:document
              [:headline [:headline-identifier "*"] [:headline-title "Headline"]
               [:headline [:headline-identifier "**"] [:headline-title "Headline 2"]
                [:headline [:headline-identifier "***"] [:headline-title "Headline 3"]]]]])

       (fact "headlines of a same level are not nested"
             (parser/parse-org "* Headline\n* Headline 2") =>
             [:document
              [:headline [:headline-identifier "*"] [:headline-title "Headline"]]
              [:headline [:headline-identifier "*"] [:headline-title "Headline 2"]]])

       (fact "headlines of a higher higher level are not nested"
             (parser/parse-org "** Headline\n* Headline 2") =>
             [:document
              [:headline [:headline-identifier "**"] [:headline-title "Headline"]]
              [:headline [:headline-identifier "*"] [:headline-title "Headline 2"]]])

       (fact "sections are carried with headlines"
             (parser/parse-org "* Headline\nsection\n** Headline 2\nsection") =>
             [:document
              [:headline [:headline-identifier "*"] [:headline-title "Headline"]
               [:section [:paragraph "section"]]
               [:headline [:headline-identifier "**"] [:headline-title "Headline 2"]
                [:section [:paragraph "section"]]]]])

       (fact "headlines are nested in the last parent"
             (parser/parse-org "* Headline\n** Headline 2\n** Headline 3\n*** Headline 4") =>
             [:document
              [:headline [:headline-identifier "*"] [:headline-title "Headline"]
               [:headline [:headline-identifier "**"] [:headline-title "Headline 2"]]
               [:headline [:headline-identifier "**"] [:headline-title "Headline 3"]
                [:headline [:headline-identifier "***"] [:headline-title "Headline 4"]]]]]))
