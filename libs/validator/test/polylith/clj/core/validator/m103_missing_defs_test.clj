(ns polylith.clj.core.validator.m103-missing-defs-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m103-missing-defs :as m103]))

(def interfaces [{:name "auth"
                  :definitions [{:name "add-two", :type "function", :arglist [{:name "x"}]}]
                  :implementing-libs ["auth"]}
                 {:name "invoice"
                  :type "interface"
                  :definitions [{:name "abc" :type "data"}
                                {:name "func1", :type "function", :arglist [{:name "a"}]}
                                {:name "func1", :type "function", :arglist [{:name "b"}]}
                                {:name "func1", :type "function", :arglist [{:name "a"} {:name "b"}]}
                                {:name "func1", :type "function", :arglist [{:name "x"} {:name "y"}]}]
                  :implementing-libs ["invoice" "invoice2"]}
                 {:name "payFment"
                  :definitions [{:name "pay", :type "function", :arglist [{:name "a"}]}
                                {:name "pay", :type "function", :arglist [{:name "b"}]}]
                  :implementing-libs ["payment"]}
                 {:name "user"
                  :type "interface"
                  :definitions [{:name "data1" :type "data"}
                                {:name "func1", :type "function", :arglist []}
                                {:name "func2", :type "function", :arglist [{:name "a"} {:name "b"}]}
                                {:name "func2", :type "function", :arglist [{:name "x"} {:name "y"}]}
                                {:name "func3", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"}]}
                                {:name "func3", :type "function", :arglist [{:name "x"} {:name "y"} {:name "z"}]}
                                {:name "func4", :type "function", :arglist [] :sub-ns "subns"}
                                {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]
                  :implementing-libs ["user1" "user2"]}])

(def libs [{:name "auth"
                  :type "lib"
                  :interface {:name "auth",
                              :definitions [{:name "add-two", :type "function", :arglist [{:name "x"}]}]}}
                 {:name "invoice"
                  :type "lib"
                  :interface {:name "invoice"
                              :definitions [{:name "abc" :type "data"}
                                            {:name "func1", :type "function", :arglist [{:name "a"}]}
                                            {:name "func1", :type "function", :arglist [{:name "a"} {:name "b"}]}]}}
                 {:name "invoice2"
                  :type "lib"
                  :interface {:name "invoice"
                              :definitions [{:name "func1", :type "function", :arglist [{:name "b"}]}
                                            {:name "func1", :type "function", :arglist [{:name "x"} {:name "y"}]}]}}
                 {:name "payment"
                  :type "lib"
                  :interface {:name "payment"
                              :definitions [{:name "pay", :type "function", :arglist [{:name "a"}]}
                                            {:name "pay", :type "function", :arglist [{:name "b"}]}]}}
                 {:name "user1"
                  :type "lib"
                  :interface {:name "user"
                              :definitions [{:name "data1" :type "data"}
                                            {:name "func1", :type "function", :arglist []}
                                            {:name "func2", :type "function", :arglist [{:name "a"} {:name "b"}]}
                                            {:name "func3", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"}]}
                                            {:name "func4", :type "function", :arglist [] :sub-ns "subns"}
                                            {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}}
                 {:name "user2"
                  :type "lib"
                  :interface {:name "user"
                              :definitions [{:name "func2", :type "function", :arglist [{:name "x"} {:name "y"}]}
                                            {:name "func3", :type "function", :arglist [{:name "x"} {:name "y"} {:name "z"}]}
                                            {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}}])

(deftest errors--when-having-a--lib-with-missing-definitionss--return-error-message
  (is (= [{:type "error",
           :code 103,
           :colorized-message "Missing definitions in invoice2's interface: abc",
           :message           "Missing definitions in invoice2's interface: abc",
           :libs ["invoice2"]}
          {:type "error",
           :code 103,
           :colorized-message "Missing definitions in user2's interface: data1, func1[], subns.func4[]",
           :message "Missing definitions in user2's interface: data1, func1[], subns.func4[]",
           :libs ["user2"]}]
         (m103/errors interfaces libs color/none))))