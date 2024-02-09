(ns ^:no-doc polylith.clj.core.workspace.interfaces)

(defn ->interface [[_ [{:keys [name interface]}]]]
  {:name (:name interface)
   :type "interface"
   :definitions (:definitions interface)
   :implementing-libs [name]})

(defn arglist [arglist]
  (mapv :name arglist))

(defn ->multi-interface [[interface-name libs]]
  (cond-> {:name interface-name
           :type "interface"
           :definitions (vec (sort-by (juxt :sub-ns :type :name arglist)
                                      (set (mapcat #(-> % :interface :definitions) libs))))}
          interface-name (assoc :implementing-libs (vec (sort (map :name libs))))))

(defn calculate
  "Calculates all interfaces, which are all definitions (data/function/macro)
   that are defined for all libs that implements an interface."
  [libs]
  (let [grouped-libs (group-by #(-> % :interface :name) libs)
        single-libs (filter #(= (-> % second count) 1) grouped-libs)
        multi-libs (filter #(> (-> % second count) 1) grouped-libs)
        single-interfaces (mapv ->interface single-libs)
        multi-interfaces (map ->multi-interface multi-libs)]
    (vec (concat single-interfaces multi-interfaces))))
