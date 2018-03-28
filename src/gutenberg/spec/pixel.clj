(ns gutenberg.spec.pixel
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [gutenberg.spec.svg :as svg]))

(def coordinate #"[0-9][0-9]")

(def tile-pattern #"[a-d]{16}([|][a-d]{16})+")

(s/def ::tile (s/and string? (partial re-matches tile-pattern)))

(s/def ::tile-name keyword?)

(s/def ::tile-set (s/map-of ::tile-name ::tile))

(s/def ::tile-loc (partial re-matches coordinate))

(s/def ::palette (s/and vector?
                        (s/cat :color-1 ::svg/color
                               :color-2 ::svg/color
                               :color-3 ::svg/color
                               :color-4 ::svg/color)))

(s/def ::palette-name keyword?)

(s/def ::palette-set (s/map-of ::palette-name ::palette))

(s/def ::actions #{:turn-right :turn-left :flip-down :flip-over})

(s/def ::tile-map
  (s/map-of ::tile-loc
            (s/and vector?
                   (s/cat :name ::tile-name
                          :palette-name ::palette-name
                          :actions (s/* ::action)))))

(s/def ::tile-doc
  (s/and vector?
         (s/cat :tiles ::tile-set
                :palette ::palette-set
                :map ::tile-map
                :pixel-size int?)))
