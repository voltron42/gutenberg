(ns gutenberg.spec.svg
  (:require [clojure.spec.alpha :as s]))

(s/def ::dim (s/alt :int int? :double double?))

(s/def ::point (s/cat :x ::dim :y ::dim))
(s/def ::point1 (s/cat :x1 ::dim :y1 ::dim))
(s/def ::point2 (s/cat :x2 ::dim :y2 ::dim))
(s/def ::center-point (s/cat :cx ::dim :cy ::dim))
(s/def ::r-point (s/cat :rx ::dim :ry ::dim))
(s/def ::size (s/cat :width ::dim :height ::dim))

(s/def ::points
  (s/cat :first-point ::point
         :second-point ::point
         :points (s/+ ::point)))

(s/def ::color-component (s/and int? (partial <= 0) (partial >= 255)))

(s/def ::rgb-color
  (s/cat :r ::color-component
         :g ::color-component
         :b ::color-component))

(s/def ::hex-color (partial re-matches #"[#][0-9A-F]{3}([0-9A-F]{3})?"))

(s/def ::named-color
  #{"aliceblue"
    "antiquewhite"
    "aqua"
    "aquamarine"
    "azure"
    "beige"
    "bisque"
    "black"
    "blanchedalmond"
    "blue"
    "blueviolet"
    "brown"
    "burlywood"
    "cadetblue"
    "chartreuse"
    "chocolate"
    "coral"
    "cornflowerblue"
    "cornsilk"
    "crimson"
    "cyan"
    "darkblue"
    "darkcyan"
    "darkgoldenrod"
    "darkgray"
    "darkgrey"
    "darkgreen"
    "darkkhaki"
    "darkmagenta"
    "darkolivegreen"
    "darkorange"
    "darkorchid"
    "darkred"
    "darksalmon"
    "darkseagreen"
    "darkslateblue"
    "darkslategray"
    "darkslategrey"
    "darkturquoise"
    "darkviolet"
    "deeppink"
    "deepskyblue"
    "dimgray"
    "dimgrey"
    "dodgerblue"
    "firebrick"
    "floralwhite"
    "forestgreen"
    "fuchsia"
    "gainsboro"
    "ghostwhite"
    "gold"
    "goldenrod"
    "gray"
    "grey"
    "green"
    "greenyellow"
    "honeydew"
    "hotpink"
    "indianred"
    "indigo"
    "ivory"
    "khaki"
    "lavender"
    "lavenderblush"
    "lawngreen"
    "lemonchiffon"
    "lightblue"
    "lightcoral"
    "lightcyan"
    "lightgoldenrodyellow"
    "lightgray"
    "lightgrey"
    "lightgreen"
    "lightpink"
    "lightsalmon"
    "lightseagreen"
    "lightskyblue"
    "lightslategray"
    "lightslategrey"
    "lightsteelblue"
    "lightyellow"
    "lime"
    "limegreen"
    "linen"
    "magenta"
    "maroon"
    "mediumaquamarine"
    "mediumblue"
    "mediumorchid"
    "mediumpurple"
    "mediumseagreen"
    "mediumslateblue"
    "mediumspringgreen"
    "mediumturquoise"
    "mediumvioletred"
    "midnightblue"
    "mintcream"
    "mistyrose"
    "moccasin"
    "navajowhite"
    "navy"
    "oldlace"
    "olive"
    "olivedrab"
    "orange"
    "orangered"
    "orchid"
    "palegoldenrod"
    "palegreen"
    "paleturquoise"
    "palevioletred"
    "papayawhip"
    "peachpuff"
    "peru"
    "pink"
    "plum"
    "powderblue"
    "purple"
    "rebeccapurple"
    "red"
    "rosybrown"
    "royalblue"
    "saddlebrown"
    "salmon"
    "sandybrown"
    "seagreen"
    "seashell"
    "sienna"
    "silver"
    "skyblue"
    "slateblue"
    "slategray"
    "slategrey"
    "snow"
    "springgreen"
    "steelblue"
    "tan"
    "teal"
    "thistle"
    "tomato"
    "turquoise"
    "violet"
    "wheat"
    "white"
    "whitesmoke"
    "yellow"
    "yellowgreen"})

(s/def ::color (s/alt :hex ::hex-color
                      :rgb ::rgb-color
                      :named ::named-color))

(s/def ::style-stroke ::color)

(s/def ::style-stroke-width ::dim)

(s/def ::style-fill ::color)

(s/def ::style-stroke-linecap #{"butt" "round" "square"})

(s/def ::style-stroke-dasharray (s/+ ::dim))

(s/def ::style-fill-rule #{(keyword "nonzero") (keyword "evenodd")})

(s/def ::style
  (s/keys :opt-un [:style/stroke
                   :style/stroke-width
                   :style/fill
                   :style/stroke-linecap
                   :style/stroke-dasharray]))

(s/def ::path-unary
  (s/cat :label #{"H" "h" "V" "v"}
         :coord ::dim))

(s/def ::path-binary
  (s/cat :label #{"M" "m" "L" "l" "T" "t"}
         :point ::point))

(s/def ::path-curve
  (s/cat :label #{"S" "s" "Q" "q"}
         :xy1 ::point1
         :xy ::point
         ))

(s/def ::path-cubic
  (s/cat :label #{"C" "c"}
         :xy1 ::point1
         :xy2 ::point2
         :xy ::point
         ))

(s/def ::path-arc
  (s/cat :label #{"A" "a"}
         :rxy ::r-point
         :x-rot ::dim
         :large ::dim
         :sweep ::dim
         :xy ::point
         ))

(s/def ::path-d
  (s/alt
    :unary ::path-unary
    :binary ::path-binary
    :curve  ::path-curve
    :cubic ::path-cubic
    :arc ::path-arc
    ))

(s/def ::shape-path
  (s/and vector?
         (s/cat :label #{(keyword "path")}
                :d ::path-d
                :style (s/? ::style))))

(s/def ::shape-rectangle
  (s/and vector?
         (s/cat :label #{(keyword "rect")}
                :xy ::point
                :size ::size
                :rxy (s/? ::r-point)
                :style (s/? ::style))))

(s/def ::shape-line
  (s/and vector?
         (s/cat :label #{(keyword "line")}
                :xy1 ::point1
                :xy2 ::point2
                :style (s/? ::style))))

(s/def ::shape-circle
  (s/and vector?
         (s/cat :label #{(keyword "circle")}
                :cxy ::center-point
                :r ::dim
                :style (s/? ::style))))

(s/def ::shape-ellipse
  (s/and vector?
         (s/cat :label #{(keyword "ellipse")}
                :cxy ::center-point
                :rxy ::r-point
                :style (s/? ::style))))

(s/def ::shape-polygon
  (s/and vector?
         (s/cat :label #{(keyword "polygon")}
                :point ::points
                :style (s/merge
                         ::style
                         (s/keys :opt-un [::style-fill-rule])))))

(s/def ::shape-polyline
  (s/and vector?
         (s/cat :label #{(keyword "polyline")}
                :point ::points
                :style (s/? ::style))))

(s/def ::shapes
  (s/alt :rect ::shape-rectangle
        :circ ::shape-circle
        :ellipse ::shape-ellipse
        :line ::shape-line
        :polyline ::shape-polyline
        :polygon ::shape-polygon
        :path ::shape-path))

(s/def ::text-spans
  (s/and vector?
         (s/cat :label #{(keyword "text")}
                :xy ::point
                :text string?
                :style (s/? ::style))))

(s/def ::text
  (s/and vector?
         (s/cat :label #{(keyword "text")}
                :xy ::point
                :text string?
                :style (s/? ::style)
                :spans (s/* :text/spans))))

(s/def ::elements
  (s/alt :text ::text
        :shapes ::shapes))

(s/def ::svg
  (s/and vector?
         (s/cat :label #{(keyword "svg")}
                :size ::size
                :viewBox (s/? (s/cat :min-x ::dim
                                     :min-y ::dim
                                     :max-x ::dim
                                     :max-y ::dim))
                :elements (s/+ ::elements))))