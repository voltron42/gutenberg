(ns gutenberg.spec.pdf
  (:require [clojure.spec.alpha :as s]
            [pred-i-kit.core :as p]))

(s/def ::available-page-sizes #{:a0
                                :a1
                                :a10
                                :a2
                                :a3
                                :a4
                                :a5
                                :a6
                                :a7
                                :a8
                                :a9
                                :arch-a
                                :arch-b
                                :arch-c
                                :arch-d
                                :arch-e
                                :b0
                                :b1
                                :b10
                                :b2
                                :b3
                                :b4
                                :b5
                                :b6
                                :b7
                                :b8
                                :b9
                                :crown-octavo
                                :crown-quarto
                                :demy-octavo
                                :demy-quarto
                                :executive
                                :flsa
                                :flse
                                :halfletter
                                :id-1
                                :id-2
                                :id-3
                                :large-crown-octavo
                                :large-crown-quarto
                                :ledger
                                :legal
                                :letter
                                :note
                                :penguin-large-paperback
                                :penguin-small-paperback
                                :postcard
                                :royal-octavo
                                :royal-quarto
                                :small-paperback
                                :tabloid})

(s/def ::literal-page-size (s/and vector?
                                  (p/exact-count 2)
                                  (s/coll-of number?)))

(s/def ::size (s/or :enum ::available-page-sizes
                    :literal ::literal-page-size))

(s/def ::orientation #{:portrait :landscape})



(s/def ::font (s/keys :opt-un [:font/family :font/ttf-name :font/encoding :font/size
                               :font/style :font/styles :rgb/color]))   ;todo

(s/def ::title string?)
(s/def ::left-margin number?)
(s/def ::right-margin number?)
(s/def ::top-margin number?)
(s/def ::bottom-margin number?)
(s/def ::subject string?)
(s/def ::author string?)
(s/def ::creator string?)
(s/def ::doc-header (s/and vector? (s/coll-of string?)))
(s/def ::watermark (s/keys :req-un [:watermark/image ::render] ;todo
                           :opt-un [::translate ::rotate ::scale])) ;todo
(s/def ::header string?)
(s/def ::letterhead (s/and vector? (s/coll-of string?)))
(s/def ::footer (s/keys :req-un [
                                 ;todo
                                 ]))
(s/def ::pages boolean?)
(s/def ::register-system-fonts? boolean?)

;todo
(s/def ::doc-elem (s/or :anchor ::anchor
                        :chapter ::chapter
                        :chart ::chart
                        :chunk ::chunk
                        :clear-double-page ::clear-double-page
                        :graphics ::graphics
                        :heading ::heading
                        :image ::image
                        :line ::line
                        :list ::list
                        :multi-column ::multi-column
                        :pagebreak ::pagebreak
                        :paragraph ::paragraph
                        :phrase ::phrase
                        :reference ::reference
                        :section ::section
                        :spacer ::spacer
                        :string ::string
                        :subscript ::subscript
                        :superscript ::superscript
                        :svg ::svg
                        :table ::table
                        :table-cell ::table-cell))

(s/def ::references (s/map-of keyword? ::doc-elem))

(s/def ::pdf
  (s/and vector?
         (s/cat :meta (s/keys :opt-un [::title ::left-margin ::right-margin ::top-margin
                                       ::bottom-margin ::subject ::size ::orientation
                                       ::author ::creator ::font ::doc-header ::watermark
                                       ::header ::letterhead ::footer ::pages ::references
                                       ::register-system-fonts?])
                :elem (s/* ::doc-elem))))