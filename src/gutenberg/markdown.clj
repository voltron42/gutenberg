(ns gutenberg.markdown
  (:require [clojure.string :as str])
  (:import (org.commonmark.parser Parser)
           (org.commonmark.ext.gfm.tables TablesExtension TableCell)
           (org.commonmark.node Node Text IndentedCodeBlock Code HtmlBlock HtmlInline BulletList Emphasis StrongEmphasis Heading OrderedList FencedCodeBlock Image Link)
           (java.util Arrays)))

(def ^:private extensions
  (Arrays/asList (into-array Object [(TablesExtension/create)])))

(def ^:private parser (-> (Parser/builder)
                          (.extensions extensions)
                          (.build)))

(defn- make-tag [^Class my-class]
  (-> my-class .getSimpleName keyword))

(defn- get-children [^Node parent]
  (loop [child (.getFirstChild parent)
         out []]
    (if (nil? child)
      out
      (recur (.getNext child) (concat out [child])))))

(defmulti ^:private get-attrs type)

(defmethod ^:private get-attrs :default [_]
  {})

(defn- to-long-hand [^Node node]
  (reduce-kv #(if (empty? %3) %1 (assoc %1 %2 %3))
             {:tag (make-tag (type node))}
             {:attrs (get-attrs node) :content (mapv to-long-hand (get-children node))}))

(defmethod ^:private get-attrs Text [^Text node]
  {:literal (.getLiteral node)})

(defmethod ^:private get-attrs HtmlInline [^HtmlInline node]
  {:literal (.getLiteral node)})

(defmethod ^:private get-attrs HtmlBlock [^HtmlBlock node]
  {:literal (.getLiteral node)})

(defmethod ^:private get-attrs Code [^Code node]
  {:literal (.getLiteral node)})

(defmethod ^:private get-attrs IndentedCodeBlock [^IndentedCodeBlock node]
  {:literal (.getLiteral node)})

(defmethod ^:private get-attrs BulletList [^BulletList node]
  {:is-tight? (.isTight node)
   :bullet-marker (.getBulletMarker node)})

(defmethod ^:private get-attrs Emphasis [^Emphasis node]
  {:open (.getOpeningDelimiter node)
   :close (.getClosingDelimiter node)})

(defmethod ^:private get-attrs StrongEmphasis [^StrongEmphasis node]
  {:open (.getOpeningDelimiter node)
   :close (.getClosingDelimiter node)})

(defmethod ^:private get-attrs Heading [^Heading node]
  {:level (.getLevel node)})

(defmethod ^:private get-attrs OrderedList [^OrderedList node]
  {:is-tight? (.isTight node)
   :delimiter (.getDelimiter node)
   :start-number (.getStartNumber node)})

(defmethod ^:private get-attrs FencedCodeBlock [^FencedCodeBlock node]
  {:fence-char (.getFenceChar node)
   :fence-indent (.getFenceIndent node)
   :fence-length (.getFenceLength node)
   :info (.getInfo node)
   :literal (.getLiteral node)})

(defmethod ^:private get-attrs Image [^Image node]
  {:title (.getTitle node)
   :destination (.getDestination node)})

(defmethod ^:private get-attrs Link [^Link node]
  {:title (.getTitle node)
   :destination (.getDestination node)})

(defmethod ^:private get-attrs TableCell [^TableCell node]
  (let [alignment (.getAlignment node)
        aligned (if (empty? alignment)
                  {}
                  {:alignment (keyword (str/lower-case (str alignment)))})]
    (assoc aligned :is-header? (.isHeader node))))

(defn parse [^String md-txt] (to-long-hand (.parse parser md-txt)))
