(ns hotel-automation.persistence
  (:require [cheshire.core :as json]
            [clojure.java.io :refer [reader writer file]]))

(def file-name "controller-state.json")

(defn persist-state [state]
  (with-open [w (writer  file-name)]
    (.write w (json/generate-string state)))
  state)


(defn retrieve-state []
  (if (.exists (file file-name))
    (with-open [rdr (reader file-name)]
      (json/parse-string (clojure.string/join "\n" (line-seq rdr))
                         keyword))))



