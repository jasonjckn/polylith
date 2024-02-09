(ns polylith.clj.core.path-finder.splitter-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.profile-src-splitter :as splitter]))

(def settings {:active-profiles #{"default" "admin"}
               :profile-to-settings {"default" {:lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}}
                                                :paths ["libs/user/src"
                                                        "libs/user/resources"
                                                        "libs/user/test"
                                                        "projects/invoice/test"]}
                                     "admin" {:lib-deps {"org.clojure/tools.deps"{:mvn/version "0.16.1264"}}
                                              :paths ["libs/admin/src"
                                                      "libs/admin/resources"
                                                      "libs/admin/test"]}}})

(deftest extract-paths--from-non-dev-project--returns-no-profile-paths
  (is (= {:profile-src-paths []
          :profile-test-paths []}
         (splitter/extract-active-profiles-paths false settings))))

(deftest extract-paths--from-dev-project--returns-src-and-test-paths-from-selected-profiles
  (is (= {:profile-src-paths  ["libs/admin/src"
                               "libs/admin/resources"
                               "libs/user/src"
                               "libs/user/resources"]
          :profile-test-paths ["libs/admin/test"
                               "libs/user/test"
                               "projects/invoice/test"]}
         (splitter/extract-active-profiles-paths true settings))))
