(ns polylith.clj.core.validator.m205-non-top-namespace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as c]
            [polylith.clj.core.validator.m205-non-top-namespace :as m205]))

(def libs [{:type "lib"
                  :name "address"
                  :non-top-namespaces [{:non-top-ns "se.wrong"
                                        :file "libs/address/src/se/wrong/address/interface.clj"}
                                       {:non-top-ns "se.wrong"
                                        :file "libs/address/src/se/wrong/address/core.clj"}]}])

(deftest warning--when-having-non-top-namespaces--return-warning
  (is (= [{:type "warning",
           :code 205,
           :message "Non top namespace se.wrong was found in address, files: libs/address/src/se/wrong/address/interface.clj, libs/address/src/se/wrong/address/core.clj",
           :colorized-message "Non top namespace se.wrong was found in address, files: libs/address/src/se/wrong/address/interface.clj, libs/address/src/se/wrong/address/core.clj"}]
         (m205/warnings libs [] c/none))))
