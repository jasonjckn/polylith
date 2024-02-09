(ns ^:no-doc polylith.clj.core.validator.m108-project-with-multi-implementing-lib
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.criterias :as c]))

(defn multi-impl-libs [{:keys [implementing-libs]}]
  (when (> (count implementing-libs) 1) implementing-libs))

(defn errors [interfaces projects disk-paths color-mode]
  (let [{:keys [unmerged]} (common/find-project "dev" projects)
        {:keys [paths]} unmerged
        path-entries (extract/from-paths paths disk-paths)
        ; We can't use src-paths and test-paths from the dev project
        ; because it has the paths from the active profile baked in.
        lib-names (set (select/names path-entries c/lib?))
        multi-libs (set (mapcat multi-impl-libs interfaces))
        illegal-libs (sort (set/intersection lib-names multi-libs))
        libs-msg (str/join ", " (map #(color/lib % color-mode) illegal-libs))
        message (str "Components with an interface that is implemented by more than one lib "
                     "are not allowed for the " (color/project "development" color-mode) " project. "
                     "They should be added to development profiles instead: " libs-msg)]
    (when (seq illegal-libs)
      [(util/ordered-map :type "error"
                         :code 108
                         :message (color/clean-colors message)
                         :colorized-message message)])))
