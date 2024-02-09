(ns polylith.clj.core.validator.m108-project-with-multi-implementing-lib-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m108-project-with-multi-implementing-lib :as m108]))

(def interfaces [{:name "user"
                  :implementing-libs ["admin" "user"]}])

(def projects [{:alias      "dev"
                :unmerged   {:paths {:src ["development/src"
                                           "libs/user/src"]
                                     :test ["libs/user/test"]}}}])

(deftest errors--when-the-development-project-contains-multi-implementing-lib--return-an-error-message
  (is (= [{:type "error"
           :code 108
           :message "Components with an interface that is implemented by more than one lib are not allowed for the development project. They should be added to development profiles instead: user",
           :colorized-message "Components with an interface that is implemented by more than one lib are not allowed for the development project. They should be added to development profiles instead: user"}]
         (m108/errors interfaces projects [] color/none))))
