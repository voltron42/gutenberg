(ns gutenberg.spec.pixel-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [gutenberg.spec.pixel :as pixel]
            [clojure.string :as str]))

(deftest test-pixel
  (is
    (= nil
       (s/explain-data
         ::pixel/tile
         (str/join
           "|"
           ["aaaaaaaaaaaaaaaa"
            "abbbbbbbbdcbbbbb"
            "abcccdddddcbddbd"
            "abccccddddcddddd"
            "abdccccdddcddddd"
            "abddcccccccccccc"
            "abdddcccbbbbdcbb"
            "abdddcccdddddcdd"
            "abdddcbdccdddcdd"
            "abbddcbdcccccccc"
            "abdddcccccccbbbb"
            "abdddcdddcccdddd"
            "abbddcbddcbccccc"
            "acccccbddcbdccdd"
            "addddcbddcbdcccc"
            "abdddcbddcbdcdcc"])))))
