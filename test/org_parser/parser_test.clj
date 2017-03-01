(ns org-parser.parser-test
  (:use midje.sweet)
  (:require [org-parser.parser :as parser]))

(facts "about headline parsing"

       (fact "a top level headline can be parsed"
             (parser/parse-org "* Test 123 &$%^$%@$") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "Test 123 &$%^$%@$"]]])

       (fact "a low level headline can be parsed"
             (parser/parse-org "********* Test") =>
             [:document [:headline [:headline-identifier "*********"] [:headline-title "Test"]]])

       (fact "a headline can have a keyword"
             (parser/parse-org "* TODO Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-keyword "TODO"] [:headline-title "Test"]]])

       (fact "a headline can have a priority "
             (parser/parse-org "* [#A] Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-priority "A"] [:headline-title "Test"]]])

       (fact "a headline can have a priority and a  keyword"
             (parser/parse-org "* [#A] TODO Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-priority "A"] [:headline-keyword "TODO"] [:headline-title "Test"]]]))
