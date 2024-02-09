(ns ^:no-doc polylith.clj.core.workspace.core
  (:require [polylith.clj.core.antq.ifc :as antq]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.lib.interface :as bil]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace.settings :as s]
            [polylith.clj.core.workspace.base :as base]
            [polylith.clj.core.workspace.lib :as lib]
            [polylith.clj.core.workspace.project :as project]
            [polylith.clj.core.workspace.interfaces :as interfaces]))

(defn brick->lib-imports [bricks]
  (into {} (map (juxt :name :lib-imports)) bricks))

(defn brick->loc [bricks]
  (into {} (map (juxt :name :lines-of-code)) bricks))

(defn project-sorter [{:keys [is-dev name]}]
  [is-dev name])

(defn enrich-workspace [{:keys [ws-dir user-input settings configs libs bases config-errors projects paths] :as workspace}]
  (if (common/invalid-workspace? workspace)
    workspace
    (let [{:keys [top-namespace interface-ns color-mode]} settings
          suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
          interfaces (interfaces/calculate libs)
          interface-names (into (sorted-set) (keep :name) interfaces)
          calculate-latest-version? (common/calculate-latest-version? user-input)
          library->latest-version (antq/library->latest-version configs calculate-latest-version?)
          outdated-libs (bil/outdated-libs library->latest-version)
          enriched-libs (mapv #(lib/enrich ws-dir suffixed-top-ns interface-names outdated-libs library->latest-version user-input settings %) libs)
          enriched-bases (mapv #(base/enrich ws-dir suffixed-top-ns bases interface-names outdated-libs library->latest-version user-input settings %) bases)
          enriched-bricks (into [] cat [enriched-libs enriched-bases])
          brick->loc (brick->loc enriched-bricks)
          brick->lib-imports (brick->lib-imports enriched-bricks)
          enriched-settings (s/enrich-settings settings projects)
          enriched-projects (vec (sort-by project-sorter (mapv #(project/enrich-project % ws-dir enriched-libs enriched-bases suffixed-top-ns brick->loc brick->lib-imports paths user-input enriched-settings outdated-libs library->latest-version) projects)))
          messages (validator/validate-ws suffixed-top-ns enriched-settings paths interface-names interfaces enriched-libs enriched-bases enriched-projects config-errors interface-ns user-input color-mode)]
      (-> workspace
          (assoc :settings enriched-settings
                 :interfaces interfaces
                 :libs enriched-libs
                 :bases enriched-bases
                 :projects enriched-projects
                 :messages messages)
          (dissoc :config-errors)))))
