(ns gutenberg.pixel-test
  (:require [clojure.test :refer :all]
            [gutenberg.pixel :as pixel]
            [gutenberg.img :as img]
            [clojure.string :as str]
            [clojure.pprint :as pprint]
            [clojure.xml :as xml]
            [clojure.pprint :as pp]
            [clojure.java.io :as io])
  (:import (java.io FileOutputStream)))

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

(defn- build-files [xml-file img-file tile-doc]
  (let [svg (pixel/build-svg-from-tile-doc tile-doc)
        _ (pp/pprint svg)
        xml (->> svg
                 (xml/emit)
                 (with-out-str))
        out (FileOutputStream. (io/file (str "resources/" img-file)))]
    (spit (str "resources/" xml-file) xml)
    (img/rasterize :png {} svg out)))

(deftest test-pixel-small
  (build-files "one-tile.xml" "small.png"
               [{:corner corner-tile}
                {:wall ["white" "pink" "black" "purple"]}
                [:corner :wall :flip-over "00"]]))

(deftest test-pixel-big
  (build-files "tiles.xml" "map.png"
               [{:corner corner-tile
                 :wall wall-tile
                 :floor floor-tile}
                {:floor ["lightgrey" "grey" "darkgrey"]
                 :wall ["white" "pink" "black" "purple"]}
                10
                [:corner :wall "00"]
                [:corner :wall :flip-down "07"]
                [:corner :wall :flip-over "90"]
                [:corner :wall :flip-down :flip-over "97"]
                [:wall :wall "10" "20" "30" "40" "50" "60" "70" "80"]
                [:wall :wall :flip-down "17" "27" "37" "47" "57" "67" "77" "87"]
                [:wall :wall :turn-left "01" "02" "03" "04" "05" "06"]
                [:wall :wall :turn-left :flip-over "91" "92" "93" "94" "95" "96"]
                (into [:floor :floor] (for [x (range 1 9) y (range 1 7)] (str x y)))]))
