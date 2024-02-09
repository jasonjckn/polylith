(ns ^:no-doc polylith.clj.core.validator.m103-missing-defs
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [polylith.clj.core.validator.shared :as shared]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface :as util]))

(defn ->data-ifc [{:keys [definitions]}]
  (set (filter #(= "data" (:type %)) definitions)))

(defn lib-data-defs [interface lib]
  (let [data-defs (->data-ifc interface)
        comp-defs (->data-ifc (:interface lib))
        missing-defs (set/difference data-defs comp-defs)]
    (when (-> missing-defs empty? not)
      [(str/join ", " (map shared/full-name missing-defs))])))

(defn function-or-macro? [{:keys [type]}]
  (not= "data" type))

(defn functions-and-macros [{:keys [definitions]}]
  (set (filter function-or-macro? definitions)))

(defn lib-fn-defs [lib interface-functions]
  (let [lib-functions-and-macros (-> lib :interface functions-and-macros)
        missing-functions-and-macros (set/difference interface-functions lib-functions-and-macros)]
    (when (-> missing-functions-and-macros empty? not)
      (vec (sort (map shared/->function-or-macro missing-functions-and-macros))))))

(defn lib-error [interface {:keys [name] :as lib} interface-functions color-mode]
  (let [lib-defs (concat (lib-data-defs interface lib)
                               (lib-fn-defs lib interface-functions))]
    (when (-> lib-defs empty? not)
      (let [message (str "Missing definitions in "  (color/lib name color-mode) "'s interface: "
                         (str/join ", " lib-defs))]
        [(util/ordered-map :type "error"
                           :code 103
                           :message (color/clean-colors message)
                           :colorized-message message
                           :libs [name])]))))

(defn interface-errors [{:keys [implementing-libs] :as interface}
                        name->lib color-mode]
  (let [interface-functions (set (mapcat second
                                         (filter #(= 1 (-> % second count) 1)
                                                 (group-by shared/function->id
                                                           (functions-and-macros interface)))))
        ifc-libs (map name->lib implementing-libs)]
    (mapcat #(lib-error interface % interface-functions color-mode)
            ifc-libs)))

(defn errors [interfaces libs color-mode]
  (let [name->lib (into {} (map (juxt :name identity) libs))]
    (vec (mapcat #(interface-errors % name->lib color-mode)
                 interfaces))))
