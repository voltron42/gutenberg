(ns gutenberg.pixel
  (:require [clojure.spec.alpha :as s])
  (:import (clojure.lang ExceptionInfo)))

(defn build-svg-from-tile-doc [tile-doc]
  (when-let [error (s/explain-data ::pixel/tile-doc tile-doc)]
    (throw (ExceptionInfo. "Error in tile-doc" {:error error})))
  (let [conformed (s/conform ::pixel/tile-doc tile-doc)

        ]

    ))