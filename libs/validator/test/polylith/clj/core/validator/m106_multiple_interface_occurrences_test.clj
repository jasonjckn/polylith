(ns polylith.clj.core.validator.m106-multiple-interface-occurrences-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m106-multiple-interface-occurrences :as m106]))

(def libs [{:name "change"
                  :type "lib"
                  :interface {:name "change"}}
                 {:name "shell"
                  :type "lib"
                  :interface {:name "shell"}}
                 {:name "shell2"
                  :type "lib"
                  :interface {:name "shell"}}
                 {:name "file"
                  :type "lib"
                  :interface {:name "file"}}
                 {:name "git"
                  :type "lib"
                  :interface {:name "git"}}])

(def projects [{:name "core"
                :group "core"
                :type "project"
                :lib-names {:src ["change" "shell" "shell2"]}
                :base-names {:src ["tool"]}
                :paths {:src ["bases/tool/src"
                              "libs/change/src"
                              "libs/shell/src"
                              "libs/shell2/src"]}}])

(deftest errors--when-more-than-one-lib-implements-the-same-interface-in-an-project--return-error-message
  (is (= [{:type "error"
           :code 106
           :colorized-message "More than one lib that implements the shell interface was found in the core project: shell, shell2"
           :message           "More than one lib that implements the shell interface was found in the core project: shell, shell2"
           :interface "shell"
           :libs ["shell" "shell2"]
           :project "core"}]
         (m106/errors libs projects color/none))))
