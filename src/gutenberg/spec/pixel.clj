(ns gutenberg.spec.pixel
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [gutenberg.spec.svg :as svg]))

(def coordinate #"[0-9][0-9]")

(def tile-pattern #"[a-z]{16}([|][a-z]{16})+")

(s/def ::tile (s/and string? (partial re-matches tile-pattern)))

(s/def ::tile-name keyword?)

(s/def ::tile-set (s/map-of ::tile-name ::tile))

(s/def ::tile-loc (s/and string? (partial re-matches coordinate)))

(s/def ::palette (s/and vector?
                        (s/cat :colors (s/+ ::svg/color))))

(s/def ::palette-name keyword?)

(s/def ::palette-set (s/map-of ::palette-name ::palette))

(s/def ::action #{:turn-right :turn-left :flip-down :flip-over})

(s/def ::tile-listing
     (s/and vector?
            (s/cat :name ::tile-name
                   :palette-name ::palette-name
                   :actions (s/* ::action)
                   :locs (s/+ ::tile-loc)
                   )))

(s/def ::tile-doc
  (s/and vector?
         (s/cat :tiles ::tile-set
                :palette ::palette-set
                :pixel-size (s/? int?)
                :list (s/+ ::tile-listing)
                )))
