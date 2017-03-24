(ns org-parser.parser
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.core.match :refer [match]])
  (:use [com.rpl.specter]))

(defn- is-element?
  "Check if the current location is an element of the given type."
  [location type]
  (if (= location nil) false
      (->> (zip/node location)
           (selected-any? [vector? ALL (fn [x] (= x type))]))))

(defn- get-headline-level [location]
  (as-> (zip/node location) n
    (if (vector? n) n [n])
    (select-first [ALL vector? ALL string?] n)
    (count n)))

(defn- get-parent-headline-level-candidate
  "Get the best candidate for a parent headline of the current headline as a headline level."
  [headline-level parent-headline-levels]
  (as-> parent-headline-levels n
    (filter (fn [x] (< x headline-level)) n)
    (sort n)
    (last n)
    (or n 0)))

(defn- compare-headline-levels
  "Compare two headline levels with the provided comparator."
  [comparator headline-level other-headline-level]
  (and (< 0 headline-level)
       (< 0 other-headline-level)
       (comparator headline-level other-headline-level)))

(defn- get-highest-headline-level
  "Given a sequence of headline levels return the headline level that should be highest up the document tree."
  [parent-headline-levels]
  (as-> parent-headline-levels n
    (sort n)
    (first n)
    (or n 0)))

(defn- get-action
  "Figure out the action that should be take this iteration."
  [location parent-headline-levels node-to-move parent-headline-level headline-level parent-headline-level-candidate]
  (let [is-headline? (is-element? location :headline)
        is-nested? (is-element? (zip/up location) :headline)
        has-node-to-move? (not= node-to-move nil)
        highest-parent-headline-level (get-highest-headline-level parent-headline-levels)
        parent-candidate-is-valid? (compare-headline-levels < parent-headline-level-candidate headline-level)
        headline-level-higher-than-parents? (compare-headline-levels <= headline-level highest-parent-headline-level)
        at-parent? (compare-headline-levels = headline-level parent-headline-level)]
    (if has-node-to-move?
      (if (and is-headline? at-parent?)
        :add-child-to-parent
        :walk-up-tree)
      (cond (and is-headline? is-nested?) :add-level-to-parent-headline-levels
            (and is-headline? (not is-nested?) parent-candidate-is-valid?) :remove-child
            (and is-headline? (or headline-level-higher-than-parents? (empty? parent-headline-levels))) :restart-parent-headline-levels
            :else :walk-down-tree))))

(defn- get-next-location
  "Get the value for the location variables for the next iteration."
  [location node-to-move action]
  (match [action]
         [(:or :walk-down-tree :restart-parent-headline-levels :add-level-to-parent-headline-levels)] (zip/next location)
         [:walk-up-tree] (zip/prev location)
         [:remove-child] (zip/remove location)
         [:add-child-to-parent] (let [new-node (conj (zip/node location) node-to-move)]
                                  (zip/next (zip/replace location new-node)))))

(defn- get-next-parent-headline-levels
  "Get the value for the parent headline levels variable for the next iteration."
  [parent-headline-levels headline-level action]
  (match [action]
         [(:or :walk-down-tree :walk-up-tree :remove-child)] parent-headline-levels
         [(:or :restart-parent-headline-levels :add-child-to-parent)] [headline-level]
         [:add-level-to-parent-headline-levels] (conj parent-headline-levels headline-level)))

(defn- get-next-node-to-move
  "Get the value for the node to move variable for the next iteration."
  [location node-to-move action]
  (match [action]
         [(:or :walk-up-tree :walk-down-tree :restart-parent-headline-levels :add-level-to-parent-headline-levels)] node-to-move
         [:remove-child] (zip/node location)
         [:add-child-to-parent] nil))

(defn- get-next-parent-headline-level
  "Get the value for the parent headline level for the next iteration."
  [parent-headline-level parent-headline-level-candidate action]
  (match [action]
         [(:or :walk-up-tree :walk-down-tree :restart-parent-headline-levels :add-level-to-parent-headline-levels)] parent-headline-level
         [:remove-child] parent-headline-level-candidate
         [:add-child-to-parent] 0))

(defn- nest-headlines
  "Given a hiccup tree of an org document with all headlines as top level elements return a org document witht he headlines nested appropriately."
  [document]
  (loop [location (zip/vector-zip document)
         parent-headline-levels []
         node-to-move nil
         parent-headline-level 0]
    (if (zip/end? location)
      (zip/root location)
      (let [headline-level (get-headline-level location)
            parent-headline-level-candidate (get-parent-headline-level-candidate (get-headline-level location) parent-headline-levels)
            action (get-action location parent-headline-levels node-to-move parent-headline-level headline-level parent-headline-level-candidate)]
        (recur (get-next-location location node-to-move action)
               (get-next-parent-headline-levels parent-headline-levels headline-level action)
               (get-next-node-to-move location node-to-move action)
               (get-next-parent-headline-level parent-headline-level parent-headline-level-candidate action))))))

(def org-parser (insta/parser (io/resource "org.bnf")))

(defn parse-org
  "Parse an org document"
  [org-doc]
  (->> (org-parser org-doc)
       (insta/transform
        {:headline-title (fn [& args] [:headline-title (apply str args)],)
         :headline-tag (fn [& args] [:headline-tag (apply str args)])})
       (nest-headlines)))
