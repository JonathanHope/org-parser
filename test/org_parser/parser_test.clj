(ns org-parser.parser-test
  (:use midje.sweet)
  (:require [org-parser.parser :as parser]))

(facts "about headline elements"

       (fact "a headline can have a title"
             (parser/parse-org "* Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "Test"]]])

       (fact "a headline can have a title and keyword"
             (parser/parse-org "* TODO Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-keyword "TODO"] [:headline-title "Test"]]])

       (fact "a headline can have a title and priority"
             (parser/parse-org "* [#A] Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-priority "A"] [:headline-title "Test"]]])

       (fact "a headline can have a title and tags"
             (parser/parse-org "* Test :tag:tag2:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "Test"] [:headline-tags [:headline-tag "tag"] [:headline-tag "tag2"]]]])

       (fact "a headline can have a title, keyword, and priority"
             (parser/parse-org "* TODO [#A] Test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-keyword "TODO"] [:headline-priority "A"] [:headline-title "Test"]]])

       (fact "a headline can have a title, keyword, and tags"
             (parser/parse-org "* TODO Test :tag:tag2:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-keyword "TODO"] [:headline-title "Test"] [:headline-tags [:headline-tag "tag"] [:headline-tag "tag2"]]]])

       (fact "a headline can have a title, priority, and tags"
             (parser/parse-org "* [#A] Test :tag:tag2:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-priority "A"] [:headline-title "Test"] [:headline-tags [:headline-tag "tag"] [:headline-tag "tag2"]]]])

       (fact "a headline can have a title, keyword, priority, and tags"
             (parser/parse-org "* TODO [#A] Test :tag:tag2:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-keyword "TODO"] [:headline-priority "A"] [:headline-title "Test"] [:headline-tags [:headline-tag "tag"] [:headline-tag "tag2"]]]])

       (fact "a headline can have its elements seperated by multiple spaces"
             (parser/parse-org "*   TODO   [#A]   Test 123   :tag:tag2:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-keyword "TODO"] [:headline-priority "A"] [:headline-title "Test 123"] [:headline-tags [:headline-tag "tag"] [:headline-tag "tag2"]]]])

       (fact "a headline can have a title with any characer but a line break"
             (parser/parse-org "* 123094jas dlksa dne!@#%$^ $%*@#$") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "123094jas dlksa dne!@#%$^ $%*@#$"]]])

       (fact "a headline with an unsupported keyword includes it in the title"
             (parser/parse-org "* KEYWORD test") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "KEYWORD test"]]])

       (fact "a headline with invalid tags has the tags included in the title"
             (parser/parse-org "* Test tag:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "Test tag:"]]]
             (parser/parse-org "* Test :tag") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "Test :tag"]]])

       (fact "a headline with invalid tags has the tags included in the title"
             (parser/parse-org "* Test tag:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "Test tag:"]]]
             (parser/parse-org "* Test :^:") =>
             [:document [:headline [:headline-identifier "*"] [:headline-title "Test :^:"]]]))
