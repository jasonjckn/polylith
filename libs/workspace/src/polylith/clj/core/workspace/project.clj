(ns ^:no-doc polylith.clj.core.workspace.project
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.interface :as proj-deps]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as bil]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.workspace.loc :as loc]))

(defn file-exists [ws-dir cleaned-path]
  (file/exists (str ws-dir "/" cleaned-path)))

(defn project-total-loc [brick-names brick->loc]
  {:src (apply + (filter identity (map #(-> % brick->loc :src) brick-names)))
   :test (apply + (filter identity (map #(-> % brick->loc :test) brick-names)))})

(defn source-imports [brick-names brick->lib-imports source-key]
  (-> (mapcat #(-> % brick->lib-imports source-key) brick-names)
      set sort vec))

(defn project-lib-imports [brick-names brick->lib-imports]
  (let [src (source-imports brick-names brick->lib-imports :src)
        test (source-imports brick-names brick->lib-imports :test)]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))

(defn enrich-project [{:keys [name type is-dev maven-repos namespaces paths lib-deps project-lib-deps] :as project}
                      ws-dir
                      libs
                      bases
                      suffixed-top-ns
                      brick->loc
                      brick->lib-imports
                      disk-paths
                      user-input
                      settings
                      outdated-libs
                      library->latest-version]
  (let [alias (get-in settings [:projects name :alias])
        enriched-maven-repos (apply merge maven-repos (mapcat :maven-repos (concat libs bases)))
        lib-entries (extract/from-library-deps is-dev lib-deps settings)
        project-lib-entries (extract/from-library-deps is-dev project-lib-deps settings)
        path-entries (extract/from-unenriched-project is-dev paths disk-paths settings)
        lib-names-src (select/names path-entries c/lib? c/src? c/exists?)
        lib-names-test (select/names path-entries c/lib? c/test? c/exists?)
        lib-names (cond-> {}
                                (seq lib-names-src) (assoc :src lib-names-src)
                                (seq lib-names-test) (assoc :test lib-names-test))
        base-names-src (select/names path-entries c/base? c/src? c/exists?)
        base-names-test (select/names path-entries c/base? c/test? c/exists?)
        base-names (cond-> {}
                           (seq base-names-src) (assoc :src base-names-src)
                           (seq base-names-test) (assoc :test base-names-test))
        all-brick-names (concat lib-names-src base-names-src lib-names-test base-names-test)
        brick-names-to-test (common/brick-names-to-test settings name all-brick-names)
        deps (proj-deps/project-deps libs bases lib-names-src lib-names-test base-names-src base-names-test suffixed-top-ns brick-names-to-test)

        lib-imports (project-lib-imports all-brick-names brick->lib-imports)

        lines-of-code-total (project-total-loc all-brick-names brick->loc)
        lines-of-code (assoc (loc/lines-of-code ws-dir namespaces) :total lines-of-code-total)
        src-lib-deps (select/lib-deps lib-entries c/src?)
        test-lib-deps (select/lib-deps lib-entries c/test?)
        project-lib-deps (bil/lib-deps-with-latest-version name
                                                           type
                                                           {:src (select/lib-deps project-lib-entries c/src?)
                                                            :test (select/lib-deps project-lib-entries c/test?)}
                                                           outdated-libs library->latest-version
                                                           user-input
                                                           settings)
        src-paths (select/paths path-entries c/src?)
        test-paths (select/paths path-entries c/test?)
        source-paths (cond-> {}
                             (seq src-paths) (assoc :src src-paths)
                             (seq test-paths) (assoc :test test-paths))
        source-lib-deps (cond-> {}
                                (seq src-lib-deps) (assoc :src src-lib-deps)
                                (seq test-lib-deps) (assoc :test test-lib-deps))]
    (-> project
        (merge {:alias alias
                :lines-of-code lines-of-code
                :lib-names lib-names
                :base-names base-names
                :deps deps
                :paths source-paths
                :lib-deps source-lib-deps
                :project-lib-deps project-lib-deps
                :lib-imports lib-imports})
        (cond-> enriched-maven-repos (assoc :maven-repos enriched-maven-repos)
                is-dev (assoc :unmerged {:paths paths
                                         :lib-deps lib-deps})))))