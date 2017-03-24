(ns org-parser.macros)

(defmacro def- [sym init] `(def ~(with-meta sym {:private true}) ~init))
