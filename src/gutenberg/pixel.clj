(ns gutenberg.pixel
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [gutenberg.spec.pixel :as pixel]
            [clojure.pprint :as pp])
  (:import (clojure.lang ExceptionInfo)))

(defmulti ^:private process-color first)

(defmethod ^:private process-color :default [[_ str-val]] str-val)

(defmethod ^:private process-color :rgb [[_ {:keys [r g b]}]] (str "rgb(" r "," g "," b ")"))

(defmulti ^:private do-action
          (fn [_ action]
            action))

(defmethod ^:private do-action :default [point _] point)

(defmethod ^:private do-action :turn-right
  [{:keys [x y]} _] {:x (- 15 y) :y x})

(defmethod ^:private do-action :turn-left
  [{:keys [x y]} _] {:x y :y (- 15 x)})

(defmethod ^:private do-action :flip-down
  [{:keys [x y]} _] {:x x :y (- 15 y)})

(defmethod ^:private do-action :flip-over
  [{:keys [x y]} _] {:x (- 15 x) :y y})


(defn- parse-tile [tile]
  (let [rows (mapv vector (range 16) (str/split tile #"[|]"))
        pixels (reduce
                 (fn [out [y ^String row]]
                   (reduce
                     (fn [out ^Integer x]
                       (conj out {:c (- (Character/codePointAt row x) 97) :x x :y y}))
                     out
                     (range 16)))
                 []
                 rows)
        pixels (group-by :c pixels)
        counts (reduce-kv #(assoc %1 %2 (count %3)) {} pixels)
        bg (->> counts (sort-by second) last first)]
    {:bg bg :pixels (reduce-kv #(assoc %1 %2 (map (fn [{:keys [x y]}] {:x x :y y}) %3)) {} (dissoc pixels bg))}))

(defn- explode-svg [pixel-size defs uses width height]
  (let [tile-size (* 16 pixel-size)
        [width height] (map (partial * tile-size) [width height])]
    {:tag :svg
     :attrs {:width width :height height
             :xmlns "http://www.w3.org/2000/svg"
             :xmlns:xlink "http://www.w3.org/1999/xlink"}
     :content (into [{:tag :defs
                      :content (mapv (fn [{:keys [id bg pixels]}]
                                       {:tag :g
                                        :attrs {:id id}
                                        :content (into [{:tag :rect
                                                         :attrs {:width tile-size
                                                                 :height tile-size
                                                                 :stroke "none"
                                                                 :fill bg}}]
                                                       (map (fn [{:keys [c x y]}]
                                                              {:tag :rect
                                                               :attrs {:x (* x pixel-size)
                                                                       :y (* y pixel-size)
                                                                       :width pixel-size
                                                                       :height pixel-size
                                                                       :stroke "none"
                                                                       :fill c}})
                                                            pixels))})
                                    defs)}]
                    (reduce
                      (fn [out {:keys [id x y]}]
                        (concat out
                                [{:tag :use
                                  :attrs {:x (* x tile-size)
                                          :y (* y tile-size)
                                          :xlink:href (str "#" id)}}
                                 {:tag :rect
                                  :attrs {:x (* x tile-size)
                                          :y (* y tile-size)
                                          :width tile-size
                                          :height tile-size
                                          :stroke "black"
                                          :stroke-width 1
                                          :fill "none"}}
                                 ]))
                      []
                      uses))}))

(defn build-svg-from-tile-doc [tile-doc]
  (when-let [error (s/explain-data ::pixel/tile-doc tile-doc)]
    (throw (ExceptionInfo. "Error in tile-doc" {:error error})))
  (let [{:keys [tiles palettes pixel-size list] :or {pixel-size 1}} (s/conform ::pixel/tile-doc tile-doc)
        tiles (reduce-kv #(assoc %1 %2 (parse-tile %3)) {} tiles)
        palettes (reduce-kv #(assoc %1 %2 (mapv process-color %3)) {} palettes)
        list (into (sorted-map) (mapv vector (range) list))
        {:keys [defs uses]} (reduce-kv
                              (fn [{:keys [defs uses]} index {:keys [name palette-name actions locs]}]
                                (let [actions (if (empty? actions) [] actions)
                                      {:keys [bg pixels]} (get tiles name)
                                      pixels (reduce-kv #(assoc %1 %2 (mapv (fn [pixel]
                                                                              (reduce do-action pixel actions))
                                                                            %3))
                                                        {} pixels)
                                      palette (get palettes palette-name)
                                      id (str "tile" index)
                                      bg (get palette bg)]
                                  {:defs (conj defs {:id id
                                                     :bg bg
                                                     :pixels (reduce-kv
                                                               #(let [color (get palette %2)]
                                                                  (concat
                                                                    %1
                                                                    (mapv (fn [pixel]
                                                                            (assoc pixel :c color))
                                                                          %3)))
                                                               []
                                                               pixels)})
                                   :uses (concat uses (mapv (fn [loc]
                                                              (let [[x y] (map #(Integer/parseInt (str %)) loc)]
                                                                {:id id :x x :y y}))
                                                            locs))}))
                              {:defs [] :uses []}
                              list)
        [width height] (mapv #(inc (apply max (mapv % uses))) [:x :y])]
    (explode-svg pixel-size defs uses width height)))