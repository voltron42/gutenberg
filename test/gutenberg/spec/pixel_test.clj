(ns gutenberg.spec.pixel-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [gutenberg.spec.pixel :as pixel]
            [clojure.string :as str]))

(def corner-tile (str/join
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
         [:corner :wall "00"]))))

(deftest test-doc
  (let [tile-doc
        [{:corner corner-tile
          :wall wall-tile
          :floor floor-tile}
         {:floor ["lightgrey" "grey" "darkgrey"]
          :wall ["white" "pink" "purple" "black"]}
         10
         [:corner :wall "00"]
         [:corner :wall :flip-down "07"]
         [:corner :wall :flip-over "90"]
         [:corner :wall :flip-down :flip-over "97"]
         [:wall :wall "10" "20" "30" "40" "50" "60" "70" "80"]
         [:wall :wall :flip-down "17" "27" "37" "47" "57" "67" "77" "87"]
         [:wall :wall :turn-left "01" "02" "03" "04" "05" "06"]
         [:wall :wall :turn-left :flip-over "91" "92" "93" "94" "95" "96"]
         (into [:floor :floor] (for [x (range 1 9) y (range 1 7)] (str x y)))]]
    (is
      (= nil
         (s/explain-data
           ::pixel/tile-doc
           tile-doc)))

    (println (pr-str (s/conform ::pixel/tile-doc tile-doc)))
    )

  )
