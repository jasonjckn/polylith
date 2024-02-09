(ns ^:no-doc polylith.clj.core.validator.m106-multiple-interface-occurrences
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn project-error [[interface interface-libs] project-name test? color-mode]
  (when (and (not test?)
             (> (count interface-libs) 1))
    (let [lib-names (mapv second interface-libs)
          message (str "More than one lib that implements the " (color/interface interface color-mode)
                       " interface was found in the " (color/project project-name color-mode) " project: "
                       (color/lib (str/join ", " lib-names) color-mode))]
      [(util/ordered-map :type "error"
                         :code 106
                         :message (color/clean-colors message)
                         :colorized-message message
                         :interface interface
                         :libs lib-names
                         :project project-name)])))

(defn project-errors [{:keys [name test? lib-names]} libs color-mode]
  (let [project-libs (filter #(contains? (set (:src lib-names))
                                               (:name %)) libs)]
    (mapcat #(project-error % name test? color-mode)
            (filter first
              (group-by first (map (juxt #(-> % :interface :name) :name)
                                   project-libs))))))

(defn errors [libs projects color-mode]
  (mapcat #(project-errors % libs color-mode)
          projects))
