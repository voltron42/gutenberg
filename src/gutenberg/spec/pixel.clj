(ns gutenberg.spec.pixel
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [gutenberg.spec.svg :as svg]))

(def coordinate #"[a-z]([a-z])?")

(defn- coord-check [func]
  (fn [value]
    (let [str-val (func value)]
      (if-not (or (nil? str-val) (re-matches coordinate str-val))
        false
        (let [[low high] (map identity str-val)
              high (if (nil? high) low high)]
          (>= 0 (.compareTo (str low) (str high))))))))

(def tile-pattern #"[a-z]{16}([|][a-z]{16})+")

(def tile-pattern-1 #"([a-z]([0-9])?)*[a-z]([|](([a-z]([0-9])?)*[a-z])?){0,15}")

(s/def ::tile (s/and string? (partial re-matches tile-pattern-1)))

(s/def ::tile-name keyword?)

(s/def ::tile-set (s/map-of ::tile-name ::tile))

(s/def ::tile-loc (s/and keyword? (coord-check namespace) (coord-check name)))

(s/def ::palette (s/and vector?
                        (s/+ ::svg/color)))

(s/def ::palette-name keyword?)

(s/def ::palette-set (s/map-of ::palette-name ::palette))

(s/def ::action #{:turn-right :turn-left :flip-down :flip-over})

(s/def ::tile-listing
     (s/and vector?
            (s/cat :name ::tile-name
                   :palette-name ::palette-name
                   :actions (s/* ::action)
                   :locs (s/+ ::tile-loc))))

(s/def ::tile-doc
  (s/and vector?
         (s/cat :tiles ::tile-set
                :palettes ::palette-set
                :pixel-size (s/? int?)
                :list (s/+ ::tile-listing))))

(s/def ::doc-name keyword?)

(s/def ::tile-doc-set
  (s/and vector?
         (s/cat :tiles ::tile-set
                :palettes ::palette-set
                :pixel-size (s/? int?)
                :docs (s/map-of ::doc-name (s/and vector? (s/+ ::tile-listing))))))