(ns gutenberg.markdown-test
  (:require [clojure.test :refer :all]
            [gutenberg.markdown :as md]))

(deftest test-file
  (clojure.pprint/pprint
    (md/parse (slurp "resources/test.md"))
    (clojure.java.io/writer "resources/parsed.edn")))