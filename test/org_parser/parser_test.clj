(ns org-parser.parser-test
  (:use midje.sweet)
  (:require [org-parser.parser :as parser]))

(facts "about headline parsing"

       (fact "a top level headline can be parsed"
             (parser/parse-org "* Test 123 &$%^$%@$") =>
             [:document [:header [:header-identifier "*"] [:header-content "Test 123 &$%^$%@$"]]])

       (fact "a low level headline can be parsed"
             (parser/parse-org "********* Test") =>
             [:document [:header [:header-identifier "*********"] [:header-content "Test"]]])

       (fact "a headline can have a keyword"
             (parser/parse-org "* TODO Test") =>
             [:document [:header [:header-identifier "*"] [:header-keyword "TODO"] [:header-content "Test"]]])

       (fact "a headline can have a priority "
             (parser/parse-org "* [#A] Test") =>
             [:document [:header [:header-identifier "*"] [:header-priority "A"] [:header-content "Test"]]])

       (fact "a headline can have a priority and a  keyword"
             (parser/parse-org "* [#A] TODO Test") =>
             [:document [:header [:header-identifier "*"] [:header-priority "A"] [:header-keyword "TODO"] [:header-content "Test"]]]))
