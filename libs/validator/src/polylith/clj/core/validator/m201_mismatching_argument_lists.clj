(ns ^:no-doc polylith.clj.core.validator.m201-mismatching-argument-lists
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.validator.shared :as shared]
            [polylith.clj.core.util.interface.color :as color]))

(def types->message {#{"function"} "Function"
                     #{"macro"} "Macro"
                     #{"function" "macro"} "Function and macro"})

(defn function-warnings [[id [{:keys [sub-ns name type arglist]}]] interface lib-name name->lib color-mode]
  (let [other-lib-names (filterv #(not= % lib-name)
                                       (:implementing-libs interface))
        other-lib (-> other-lib-names first name->lib)
        other-function (first ((-> other-lib :interface shared/id->functions-or-macro) id))]

    (when (and (-> other-function nil? not)
               (not= arglist (:arglist other-function)))
      (let [[comp1 comp2] (sort [lib-name (:name other-lib)])
            function-or-macro1 (shared/->function-or-macro sub-ns name arglist)
            function-or-macro2 (shared/->function-or-macro other-function)
            functions-and-macros (sort [function-or-macro1 function-or-macro2])
            types (types->message (set [type (:type other-function)]))
            message (str types " in the " (color/lib comp1 color-mode) " lib "
                         "is also defined in " (color/lib comp2 color-mode)
                         " but with a different argument list: "
                         (str/join ", " functions-and-macros))]
        [(util/ordered-map :type "warning"
                           :code 201
                           :message (color/clean-colors message)
                           :colorized-message message
                           :libs [comp1 comp2])]))))

(defn lib-warnings [lib interfaces name->lib color-mode]
  (let [interface-name (-> lib :interface :name)
        interface (first (filter #(= interface-name (:name %)) interfaces))
        lib-name (:name lib)
        lib-id->function (-> lib :interface shared/id->functions-or-macro)
        single-id->functions (filter #(= (-> % second count) 1) lib-id->function)]
    (mapcat #(function-warnings % interface lib-name name->lib color-mode) single-id->functions)))

(defn warnings [interfaces libs color-mode]
  (let [name->lib (into {} (map (juxt :name identity) libs))]
    (set (mapcat #(lib-warnings % interfaces name->lib color-mode) libs))))
