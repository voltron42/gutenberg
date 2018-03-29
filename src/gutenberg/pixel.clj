(ns gutenberg.pixel
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str])
  (:import (clojure.lang ExceptionInfo)))

(defmulti ^:private process-color first)

(defmethod ^:private process-color :default [[_ str-val]] str-val)

(defmethod ^:private process-color :rgb [[_ {:keys [r g b]}]] (str "rgb(" r "," g "," b ")"))

(defmulti ^:private get-action identity)

(defmethod ^:private get-action :default [_] identity)

(def ^:private base-code-point 97)

(defn- parse-tile [tile]
  (let [pixels (group-by :c (reduce
                              (fn [out [y ^String row]]
                                (reduce
                                  (fn [out ^Integer x]
                                    (conj out {:c (Character/codePointAt row x) :x x :y y}))
                                  out
                                  (range 16)))
                              []
                              (mapv vector (range 16) (str/split tile #"[|]"))))
        counts (reduce-kv #(assoc %1 %2 (count %3)) {} pixels)
        bg (->> counts (sort-by second) last first)]
    {:bg bg :pixels (reduce-kv #(assoc %1 %2 (select-keys %3 [:x :y])) {} (dissoc pixels bg))}))

(defn build-svg-from-tile-doc [tile-doc]
  (when-let [error (s/explain-data ::pixel/tile-doc tile-doc)]
    (throw (ExceptionInfo. "Error in tile-doc" {:error error})))
  (let [{:keys [tiles palette pixel-size list]} (s/conform ::pixel/tile-doc tile-doc)
        tiles (reduce-kv #(assoc %1 %2 (parse-tile %3)) {} tiles)
        palette (reduce-kv #(assoc %1 %2 (map process-color %3)) {} palette)
        {:keys [defs uses]} (reduce-kv
                              (fn [{:keys [defs uses]} {:keys []}])
                              {:defs [] :uses []}
                              list)]
    (explode-svg pixel-size defs uses)
    ))