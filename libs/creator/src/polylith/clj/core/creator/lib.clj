(ns ^:no-doc polylith.clj.core.creator.lib
  (:require [polylith.clj.core.creator.brick :as brick]))

(defn create-lib [ws-dir settings lib-name interface-name is-git-add]
  (let [{:keys [top-namespace interface-ns]} settings
        libs-dir (str ws-dir "/libs/" lib-name)]
    (brick/create-resources-dir ws-dir "libs" lib-name is-git-add)
    (brick/create-config-file ws-dir "libs" lib-name is-git-add)
    (brick/create-src-ns ws-dir top-namespace libs-dir interface-ns interface-name is-git-add)
    (brick/create-test-ns ws-dir top-namespace libs-dir interface-ns interface-name interface-name is-git-add)))

(defn create [{:keys [ws-dir settings] :as workspace} lib-name interface-name is-git-add]
  (let [ifc-name (or interface-name lib-name)]
    (brick/create-brick workspace
                        lib-name
                        (fn [] (create-lib ws-dir settings lib-name ifc-name is-git-add)))))
