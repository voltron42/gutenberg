(ns gutenberg.pixel
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [gutenberg.spec.pixel :as pixel]
            [clojure.pprint :as pp])
  (:import (clojure.lang ExceptionInfo)))

(defmulti ^:private process-color first)

(defmethod ^:private process-color :default [[_ str-val]] str-val)

(defmethod ^:private process-color :rgb [[_ {:keys [r g b]}]] (str "rgb(" r "," g "," b ")"))

(defmulti ^:private get-action identity)

(defmethod ^:private get-action :default [_] identity)

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
  (let [tile-size (* 16 pixel-size)]
    (into [:svg {:viewBox (str "0 0 " width " " height)} (into [:defs]
                         (mapv (fn [{:keys [id bg pixels]}]
                                 (into
                                   [:g {:id id}
                                    [:rect {:width tile-size
                                            :height tile-size
                                            :fill bg}]]
                                   (map (fn [{:keys [c x y]}]
                                          [:rect {:x (* x pixel-size)
                                                  :y (* y pixel-size)
                                                  :width pixel-size
                                                  :height pixel-size
                                                  :fill c}])
                                        pixels)))
                               defs))]
          (mapv (fn [{:keys [id x y]}]
                  [:use {:x (* x tile-size)
                         :y (* y tile-size)
                         :href (str "#" id)}])
                uses))))

(defn build-svg-from-tile-doc [tile-doc]
  (when-let [error (s/explain-data ::pixel/tile-doc tile-doc)]
    (throw (ExceptionInfo. "Error in tile-doc" {:error error})))
  (let [{:keys [tiles palettes pixel-size list] :or {pixel-size 1}} (s/conform ::pixel/tile-doc tile-doc)
        tiles (reduce-kv #(assoc %1 %2 (parse-tile %3)) {} tiles)
        palettes (reduce-kv #(assoc %1 %2 (mapv process-color %3)) {} palettes)
        list (into (sorted-map) (mapv vector (range) list))
        {:keys [defs uses]} (reduce-kv
                              (fn [{:keys [defs uses]} index {:keys [name palette-name actions locs]}]
                                (let [{:keys [bg pixels]} (get tiles name)
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