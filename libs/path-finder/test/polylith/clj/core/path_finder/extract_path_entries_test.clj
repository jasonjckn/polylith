(ns polylith.clj.core.path-finder.extract-path-entries-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.test-data :as test-data]
            [polylith.clj.core.path-finder.interface.extract :as extract]))

(def settings {:active-profiles #{"default"}
               :profile-to-settings {"default" {:paths ["libs/user/resources"
                                                        "libs/user/src"
                                                        "libs/user/test"]}
                                     "admin" {:paths ["libs/admin/resources"
                                                      "libs/admin/src"
                                                      "libs/admin/test"]}}})

(def paths {:src ["bases/cli/resources"
                  "bases/cli/src"
                  "libs/address/resources"
                  "libs/address/src"
                  "libs/database/resources"
                  "libs/database/src"
                  "libs/invoicer/resources"
                  "libs/invoicer/src"
                  "libs/purchaser/resources"
                  "libs/purchaser/src"
                  "libs/user/resources"
                  "libs/user/src"
                  "development/src"]
            :test ["bases/cli/test"
                   "libs/address/test"
                   "libs/database/test"
                   "libs/invoicer/test"
                   "libs/purchaser/test"
                   "libs/user/test"
                   "projects/invoice/test"
                   "development/test"]})

(deftest path-entries--lists-of-paths--returns-extracted-path-entries
  (is (= test-data/path-entries
         (extract/from-unenriched-project
           true
           paths
           {:missing []}
           settings))))

(deftest profile-entries--when-two-profiles-are-extracted--return-paths
  (is (= [{:exists?    true
           :name       "user"
           :path       "libs/user/resources"
           :profile?   false
           :source-dir "resources"
           :test?      false
           :type       :lib}
          {:exists?    true
           :name       "user"
           :path       "libs/user/src"
           :profile?   false
           :source-dir "src"
           :test?      false
           :type       :lib}
          {:exists?    true
           :name       "user"
           :path       "libs/user/test"
           :profile?   false
           :source-dir "test"
           :test?      true
           :type       :lib}]
         (extract/from-profiles-paths [] settings "default"))))
