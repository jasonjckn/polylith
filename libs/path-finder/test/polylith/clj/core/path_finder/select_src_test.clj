(ns polylith.clj.core.path-finder.select-src-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.test-data :as test-data]))

(deftest all-src-paths--when-executed--returns-src-paths-from-all-libs-bases-and-entities-including-profile-paths
  (is (= ["bases/cli/resources"
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
         (select/paths test-data/path-entries c/src?))))

(deftest all-test-paths--when-executed--returns-test-paths-from-all-libs-bases-and-entities-including-profile-paths
  (is (= ["bases/cli/test"
          "libs/address/test"
          "libs/database/test"
          "libs/invoicer/test"
          "libs/purchaser/test"
          "libs/user/test"
          "development/test"
          "projects/invoice/test"]
         (select/paths test-data/path-entries c/test?))))

(deftest brick-src-entries--when-executed--returns-entries-collected-from-lib-and-base-src-paths
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
           :path       "libs/user/resources"
           :profile?   true
           :source-dir "resources"
           :test?      false
           :type       :lib}
          {:exists?    true
           :name       "user"
           :path       "libs/user/src"
           :profile?   true
           :source-dir "src"
           :test?      false
           :type       :lib}]
         (select/entries test-data/path-entries c/src? (c/=name "user")))))

(deftest src-lib-names--when-executed--returns-expected-result
  (is (= ["address"
          "database"
          "invoicer"
          "purchaser"
          "user"]
         (select/names test-data/path-entries c/lib?))))
