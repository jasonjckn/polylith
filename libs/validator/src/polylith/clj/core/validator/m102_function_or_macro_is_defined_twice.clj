(ns ^:no-doc polylith.clj.core.validator.m102-function-or-macro-is-defined-twice
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.shared :as shared]))

(defn duplicated-arglist-error [lib-name lib-duplication color-mode]
  (let [message (str "Function or macro is defined twice in " (color/lib lib-name color-mode) ": "
                     (str/join ", " (map shared/->function-or-macro lib-duplication)))]
    (util/ordered-map :type "error"
                      :code 102
                      :message (color/clean-colors message)
                      :colorized-message message
                      :libs [lib-name])))

(defn lib-errors [lib color-mode]
  (let [lib-name (:name lib)
        lib-id->function (-> lib :interface shared/id->functions-or-macro)
        multi-id-functions (mapv second (filter #(> (-> % second count) 1) lib-id->function))]
    (mapv #(duplicated-arglist-error lib-name % color-mode) multi-id-functions)))

(defn errors [libs color-mode]
  (vec (mapcat #(lib-errors % color-mode) libs)))
