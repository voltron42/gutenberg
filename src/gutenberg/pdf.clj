(ns gutenberg.pdf
  (:require [clojure.spec.alpha :as s]
            [ring.util.io :as io]
            [clj-pdf.core :as pdf])
  (:import (clojure.lang ExceptionInfo)
           (java.io OutputStream)))

(defn pdf-to-piped-stream [pdf]
  (when-let [error (s/explain-data ::pdf pdf)]
    (throw (ExceptionInfo. "Invalid PDF data" error)))
  (io/piped-input-stream
    (fn [^OutputStream out]
      (pdf/pdf pdf out)
      (.flush out))))

