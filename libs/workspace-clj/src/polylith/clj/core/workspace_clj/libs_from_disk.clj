(ns ^:no-doc polylith.clj.core.workspace-clj.libs-from-disk
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as bil]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.brick-dirs :as brick-dirs]
            [polylith.clj.core.workspace-clj.brick-paths :as brick-paths]
            [polylith.clj.core.workspace-clj.non-top-namespace :as non-top-ns]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]))

(defn read-lib [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir interface-ns lib-deps-config]
  (let [config (:deps lib-deps-config)
        lib-name (:name lib-deps-config)
        lib-dir (str ws-dir "/libs/" lib-name)
        lib-top-src-dirs (brick-dirs/top-src-dirs lib-dir top-src-dir config)
        lib-top-test-dirs (brick-dirs/top-test-dirs lib-dir top-src-dir config)
        interface-path-name (first (mapcat file/directories lib-top-src-dirs))
        interface-name (common/path-to-ns interface-path-name)
        src-dirs (mapv #(str % interface-path-name)
                       lib-top-src-dirs)
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        namespaces (ns-from-disk/namespaces-from-disk ws-dir lib-top-src-dirs lib-top-test-dirs suffixed-top-ns interface-ns)
        definitions (defs-from-disk/defs-from-disk src-dirs interface-ns)
        entity-root-path (str "libs/" lib-name)
        lib-deps (bil/brick-lib-deps ws-dir ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home)
        paths (brick-paths/source-paths lib-dir config)
        source-paths (config/source-paths config)
        non-top-namespaces (non-top-ns/non-top-namespaces "lib" lib-name lib-dir top-src-dir source-paths)]
    (util/ordered-map :name lib-name
                      :type "lib"
                      :maven-repos (:mvn/repos config)
                      :paths paths
                      :namespaces namespaces
                      :non-top-namespaces non-top-namespaces
                      :lib-deps lib-deps
                      :interface (util/ordered-map :name interface-name
                                                   :definitions definitions))))

(defn read-libs [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir interface-ns configs]
  (vec (sort-by :name (map #(read-lib ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir interface-ns %)
                           configs))))
