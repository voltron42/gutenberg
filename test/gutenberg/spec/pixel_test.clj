(ns gutenberg.spec.pixel-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [gutenberg.spec.pixel :as pixel]
            [clojure.string :as str]))

(def corner-tile
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
     "abdddcbddcbdcdcc"]))

(def edge-tile
  (str/join
    "|"
    ["aabddcbddcbdcdcc"
     "aabbdcdddcbdcdcc"
     "bbabdcccccbdcccc"
     "dbbabcbdddbdcdcc"
     "dddbacbddcbdcdcc"
     "cccccbbddccccdcc"
     "dddddbaddcddcdcc"
     "ddddddbadcbdcdbb"
     "dddddcddacbdcdcc"
     "cccccccccdbdcdcc"
     "bdcbbbbbbbadcdcc"
     "ddcddddddddbcdcc"
     "ccccccccccccdbcc"
     "dddddcdddddddbcc"
     "cccccccccccccccc"
     "cccccccccccccccc"]))

(def wall-tile
  (str/join
    "|"
    ["aaaaaaaaaaaaaaaa"
     "bdcbbbbbbdcbbbbb"
     "ddcbddbdddcbddbd"
     "ddcdddddddcddddd"
     "ddcdddddddcddddd"
     "cccccccccccccccc"
     "bbbbdcbbbbbbdcbb"
     "dddddcdddddddcdd"
     "dddddcdddddddcdd"
     "cccccccccccccccc"
     "bdcbbbbbbdcbbbbb"
     "ddcdddddddcddddd"
     "cccccccccccccccc"
     "dddddcdddddddcdd"
     "cccccccccccccccc"
     "cccccccccccccccc"]))

(def floor-tile
  (str/join
    "|"
    ["bcaabaabbcaabaaa"
     "abcaaacbbbcaaaaa"
     "aabcccbaaabcabaa"
     "aabbbbaaaaabcaba"
     "aabbaaaabacbbaaa"
     "acbaabaaacbbbcaa"
     "cbaabaaacbaaabcc"
     "bbabaaacbaaaaabb"
     "bbcaaacbaaaaaacb"
     "aabcccbaaaabacba"
     "aaabbbaaaabacbaa"
     "aaaabaababacbbaa"
     "abaabcaaaacbbbaa"
     "aabaabccccbaabca"
     "aaabaabbbbaaaabc"
     "caaaaaabbaaaaaab"]))

(deftest test-tile
  (is
    (= nil
       (s/explain-data
         ::pixel/tile
         corner-tile
         )))
  (is
    (= nil
       (s/explain-data
         ::pixel/tile-set
         {:corner corner-tile})))
  (is
    (= nil
       (s/explain-data
         ::pixel/tile-set
         {:corner corner-tile
          :wall wall-tile
          :floor floor-tile})))
  )

(deftest test-palette
  (is
    (= nil
       (s/explain-data
         ::pixel/palette
         ["lightgrey" "grey" "darkgrey"])))

  (is
    (= nil
       (s/explain-data
         ::pixel/palette
         ["white" "pink" "purple" "black"])))

  (is
    (= nil
       (s/explain-data
         ::pixel/palette-set
         {:floor ["lightgrey" "grey" "darkgrey"]
          :wall ["white" "pink" "purple" "black"]}
         )))

  )

(deftest test-tile-listing
  (is
    (= nil
       (s/explain-data
         ::pixel/tile-listing
         [:corner :wall :a/a]))))

(deftest test-tile-listing-1
  (is
    (= nil
       (s/explain-data
         ::pixel/tile-listing
         [:corner :wall :a-b/a]))))

(deftest test-doc
  (let [tile-doc
        [{:corner corner-tile
          :wall wall-tile
          :floor floor-tile}
         {:floor ["lightgrey" "grey" "darkgrey"]
          :wall ["white" "pink" "purple" "black"]}
         10
         [:corner :wall :a/a]
         [:corner :wall :flip-down :a/h]
         [:corner :wall :flip-over :j/a]
         [:corner :wall :flip-down :flip-over :j/h]
         [:wall :wall :b-i/a]
         [:wall :wall :flip-down :b-i/h]
         [:wall :wall :turn-left :a/b-g]
         [:wall :wall :turn-left :flip-over :j/b-g]
         [:floor :floor :b-i/b-g]]]
    (is
      (= nil
         (s/explain-data
           ::pixel/tile-doc
           tile-doc)))

    (println (pr-str (s/conform ::pixel/tile-doc tile-doc)))))
