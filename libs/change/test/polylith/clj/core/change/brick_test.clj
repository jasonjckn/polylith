(ns polylith.clj.core.change.brick-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.entity :as brick]))

(def files ["bases/core/src/polylith/core/main.clj"
            "bases/core/test/polylith/core/main_test.clj"
            "bases/core/test/polylith/workspace/main_test.clj"
            "libs/cmd/src/polylith/cmd/compile.clj"
            "libs/cmd/src/polylith/cmd/test.clj"
            "libs/workspace/src/polylith/core/core.clj"
            "libs/workspace/src/polylith/core/interface.clj"
            "libs/workspace/src/polylith/core/interfaces.clj"
            "deps.edn"
            "todo.txt"])

(deftest bricks--when-having-a-list-of-changed-files--return-bases-and-libs
  (is (= {:changed-bases ["core"]
          :changed-libs ["cmd" "workspace"]
          :changed-projects []}
         (brick/changed-entities files {:missing []}))))
