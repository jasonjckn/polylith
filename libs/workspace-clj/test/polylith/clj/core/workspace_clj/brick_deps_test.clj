(ns polylith.clj.core.workspace-clj.brick-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace-clj.brick-deps :as brick-deps]))

(deftest is-dev-lib-project
  (is (= false
         (brick-deps/brick-name "../../libs/invoicer" true))))

(deftest is-not-dev-lib-project
  (is (= "invoicer"
         (brick-deps/brick-name "../../libs/invoicer" false))))

(deftest is-dev-lib-project
  (is (= nil
         (brick-deps/brick-name "../../bases/mybase" true))))

(deftest is-not-dev-lib-project
  (is (= "mybase"
         (brick-deps/brick-name "../../bases/mybase" false))))

(deftest is-dev-lib
  (is (= "invoicer"
         (brick-deps/brick-name "libs/invoicer" true))))

(deftest is-not-dev-lib
  (is (= nil
         (brick-deps/brick-name "libs/invoicer" false))))

(deftest is-dev-lib
  (is (= "mybase"
         (brick-deps/brick-name "bases/mybase" true))))

(deftest is-not-dev-lib
  (is (= nil
         (brick-deps/brick-name "bases/mybase" false))))

(deftest is-dev-lib-local
  (is (= "invoicer"
         (brick-deps/brick-name "./libs/invoicer" true))))

(deftest is-not-dev-lib-local
  (is (= nil
         (brick-deps/brick-name "./libs/invoicer" false))))

(deftest is-dev-lib-local
  (is (= "mybase"
         (brick-deps/brick-name "./bases/mybase" true))))

(deftest is-not-dev-lib-local
  (is (= nil
         (brick-deps/brick-name "./bases/mybase" false))))
