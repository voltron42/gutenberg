(ns gutenberg.spec.svg
  (:require [clojure.spec.alpha :as s]
            [pred-i-kit.core :as p]))

(s/def ::dim (s/or :int int?
                   :double double?))

(s/def :dim/point
  (s/and vector?
         (p/exact-count 2)
         (s/coll-of ::dim)))
(s/def :dim/width ::dim)
(s/def :dim/height ::dim)
(s/def :dim/size ::dim/point)
(s/def :dim/x ::dim)
(s/def :dim/y ::dim)
(s/def :dim/xy ::dim/point)
(s/def :dim/x1 ::dim)
(s/def :dim/y1 ::dim)
(s/def :dim/xy1 ::dim/point)
(s/def :dim/x2 ::dim)
(s/def :dim/y2 ::dim)
(s/def :dim/xy2 ::dim/point)
(s/def :dim/r ::dim)
(s/def :dim/cx ::dim)
(s/def :dim/cy ::dim)
(s/def :dim/cxy ::dim/point)
(s/def :dim/rx ::dim)
(s/def :dim/ry ::dim)
(s/def :dim/rxy ::dim/point)
(s/def :dim/points
  (s/and vector?
         (p/min-count 3)
         (s/coll-of :dim/point)))

(s/def ::rgb-color
  (s/and vector?
         (p/exact-count 3)
         (s/coll-of (p/value-range 0 255))))

(s/def ::hex-color
  (p/matches? #"[#][0-9A-F]{6}"))

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

(s/def ::color (s/or :hex ::hex-color
                     :rgb ::rgb-color
                     :named ::named-color))

(s/def :style/stroke ::color)

(s/def :style/stroke-width number?)

(s/def :style/fill ::color)

(s/def :style/stroke-linecap
  #{"butt" "round" "square"})

(s/def :style/stroke-dasharray
  (s/and vector?
         (p/min-count 1)
         (s/coll-of ::dim)))

(s/def :style/fill-rule #{(keyword "nonzero") (keyword "evenodd")})

(s/def ::style
  (s/keys :opt-un [:style/stroke
                   :style/stroke-width
                   :style/fill
                   :style/stroke-linecap
                   :style/stroke-dasharray]))

(s/def :path/unary
  (s/and vector?
         (s/cat :label #{"H" "h" "V" "v"}
                :coord ::dim)))
(s/def :path/binary
  (s/and vector?
         (s/cat :label #{"M" "m" "L" "l" "T" "t"}
                :x ::dim
                :y ::dim)))
(s/def :path/curve
  (s/and vector?
         (s/cat :label #{"S" "s" "Q" "q"}
                :x ::dim
                :y ::dim
                :x1 ::dim
                :y1 ::dim)))
(s/def :path/cubic
  (s/and vector?
         (s/cat :label #{"C" "c"}
                :x ::dim
                :y ::dim
                :x1 ::dim
                :y1 ::dim
                :x2 ::dim
                :y2 ::dim)))
(s/def :path/arc
  (s/and vector?
         (s/cat :label #{"A" "a"}
                :rx ::dim
                :ry ::dim
                :x-rot ::dim
                :large ::dim
                ::sweep ::dim
                :x ::dim
                :y ::dim)))

(s/def :path/d
  (s/and vector?
         (s/coll-of
           (s/or
             :unary :path/unary
             :binary :path/binary
             :curve  :path/curve
             :cubic :path/cubic
             :arc :path/arc))))

(s/def :shape/path
  (s/and vector?
         (s/cat :label #{(keyword "path")}
                :d :path/d
                :style (s/? ::style))))

(s/def :shape/rectangle
  (s/and vector?
         (s/cat :label #{(keyword "rect")}
                :xy :dim/xy
                :size :dim/size
                :style (s/merge
                         ::style
                         (s/keys :opt-un [:dim/rxy])))))

(s/def :shape/line
  (s/and vector?
         (s/cat :label #{(keyword "line")}
                :xy1 :dim/xy1
                :xy2 :dim/xy2
                :style (s/? ::style))))

(s/def :shape/circle
  (s/and vector?
         (s/cat :label #{(keyword "circle")}
                :cxy :dim/cxy
                :r :dim/r
                :style (s/? ::style))))

(s/def :shape/ellipse
  (s/and vector?
         (s/cat :label #{(keyword "ellipse")}
                :cxy :dim/cxy
                :rxy :dim/rxy
                :style (s/? ::style))))

(s/def :shape/polygon
  (s/and vector?
         (s/cat :label #{(keyword "polygon")}
                :point :dim/points
                :style (s/merge
                         ::style
                         (s/keys :opt-un [:style/fill-rule])))))

(s/def :shape/polyline
  (s/and vector?
         (s/cat :label #{(keyword "polyline")}
                :point :dim/points
                :style (s/? ::style))))

(s/def ::shapes
  (s/or :rect :shape/rectangle
        :circ :shape/circle
        :ellipse :shape/ellipse
        :line :shape/line
        :polyline :shape/polyline
        :polygon :shape/polygon
        :path :shape/path))

(s/def :text/spans
  (s/and vector?
         (s/cat :label #{(keyword "text")}
                :xy :dim/xy
                :style (s/? ::style)
                :text string?)))

(s/def ::text
  (s/and vector?
         (s/cat :label #{(keyword "text")}
                :xy :dim/xy
                :style (s/? ::style)
                :text string?
                :spans (s/* :text/spans))))