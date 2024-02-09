(ns ^:no-doc polylith.clj.core.validator.m207-unnecessary-libs-in-project
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn lib-deps [[ _ {:keys [src test]}]]
  (concat (:direct src)
          (:indirect src)
          (:direct test)
          (:indirect test)))

(defn warning [{:keys [is-dev name lib-names deps]} project->settings check-dev color-mode]
  "Warns if a lib is not used by any brick in the project for all
   project except development (which can be included by passing in :dev).
   Components can be excluded from the check by putting it in the :necessary
   vector for a project in :projects in workspace.edn."
  (let [{:keys [src test]} lib-names
        necessary (-> (project->settings name) :necessary set)
        defined-libs (set/difference (set (concat src test)) necessary)
        used-libs (set (mapcat lib-deps deps))
        unused-libs (str/join ", " (map #(color/lib % color-mode)
                                              (sort (set/difference defined-libs used-libs))))]
    (when (and (or check-dev (not is-dev))
               (seq unused-libs))
      (let [message (str "Unnecessary libs were found in the " (color/project name color-mode)
                         " project and may be removed: " unused-libs ". To ignore this warning,"
                         " execute 'poly help check' and follow the instructions for warning 207.")]
        [(util/ordered-map :type "warning"
                           :code 207
                           :message (color/clean-colors message)
                           :colorized-message message)]))))

(defn warnings [cmd settings projects dev? color-mode]
  (let [project->settings (:projects settings)
        check-dev (and dev? (= "check" cmd))]
    (mapcat #(warning % project->settings check-dev color-mode) projects)))
