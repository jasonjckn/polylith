(ns polylith.clj.core.validator.m203-path-exists-in-both-dev-and-profile-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m203-path-exists-in-both-dev-and-profile :as m203]))

(def settings {:profile-to-settings {"default" {:paths ["libs/user/src"
                                                        "libs/user/test"]}
                                     "admin" {:paths ["libs/admin/src"
                                                      "libs/admin/test"
                                                      "libs/invoice/src"]}}})

(def projects [{:alias        "dev"
                :unmerged {:paths {:src ["libs/invoice/src"
                                         "development/src"]}}}])

(deftest warnings--path-was-found-in-both-dev-and-a-profile--returns-error-message
  (is (= [{:code 203
           :type "warning"
           :message "The same path exists in both the development project and the admin profile: libs/invoice/src"
           :colorized-message "The same path exists in both the development project and the admin profile: libs/invoice/src"}]
         (m203/warnings settings projects color/none))))
