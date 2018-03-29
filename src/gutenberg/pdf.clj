(ns gutenberg.pdf
  (:require [clojure.spec.alpha :as s]
            [ring.util.io :as io]
            [clj-pdf.core :as pdf]
            [gutenberg.spec.pdf :as spec])
  (:import (clojure.lang ExceptionInfo)
           (java.io OutputStream)))

(defn pdf-to-piped-stream [pdf out]
  (when-let [error (s/explain-data ::spec/pdf pdf)]
    (throw (ExceptionInfo. "Invalid PDF data" error)))
  (pdf/pdf pdf out))

