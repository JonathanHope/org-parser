(ns org-parser.parser-test
  (:use midje.sweet)
  (:require [org-parser.parser :as parser]))

(facts "about headline parsing"

       (fact "a headline can be without a keyword, priority, or tag"
             (parser/parse-org "* Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "Test"]]])

       (fact "a headline can have a keyword"
             (parser/parse-org "* TODO Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-keyword "TODO"] [:headline-title "Test"]]])

       (fact "a headline can have a priority "
             (parser/parse-org "* [#A] Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-priority "A"] [:headline-title "Test"]]])

       (fact "a headline can have tags "
             (parser/parse-org "* Test :tag:tag2:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "Test"] [:headline-tags [:headline-tag "tag"] [:headline-tag "tag2"]]]])

       (fact "a headline can have a priority and a  keyword"
             (parser/parse-org "* [#A] TODO Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-priority "A"] [:headline-keyword "TODO"] [:headline-title "Test"]]])

       (fact "a headline can have a keyword and tags"
             (parser/parse-org "* TODO Test :tag:tag2:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-keyword "TODO"] [:headline-title "Test"] [:headline-tags [:headline-tag "tag"] [:headline-tag "tag2"]]]])

       (fact "a headline can have a priority and tags"
             (parser/parse-org "* [#A] Test :tag:tag2:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-priority "A"] [:headline-title "Test"] [:headline-tags [:headline-tag "tag"] [:headline-tag "tag2"]]]])

       (fact "a headline can have a keyword, priority, and tags"
             (parser/parse-org "* [#A] TODO Test :tag:tag2:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-priority "A"] [:headline-keyword "TODO"] [:headline-title "Test"] [:headline-tags [:headline-tag "tag"] [:headline-tag "tag2"]]]]))
