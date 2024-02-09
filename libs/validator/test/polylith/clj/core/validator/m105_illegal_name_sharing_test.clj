(ns polylith.clj.core.validator.m105-illegal-name-sharing-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m105-illegal-name-sharing :as m105]))

(deftest errors--when-a-base-and-an-interface-but-not-a-lib-shares-the-same-name--returns-error
  (let [interfaces '[{:name "mybase"}]
        libs '[{:name "mybase1"
                      :interface {:name "mybase"}}]
        bases [{:name "mybase"}]
        interface-names (set (map :name interfaces))]
    (is (= [{:type "error"
             :code 105
             :colorized-message "A base can't have the same name as an interface or lib: mybase"
             :message           "A base can't have the same name as an interface or lib: mybase"
             :interfaces ["mybase"]
             :libs []
             :bases ["mybase"]}]
           (m105/errors interface-names libs bases color/none)))))

(deftest errors--when-a-base-and-a-lib-the-same-name--returns-error
  (let [interfaces '[{:name "mybase"}]
        libs '[{:name "mybase"
                      :interface {:name "mybase"}}]
        bases [{:name "mybase"}]
        interface-names (set (map :name interfaces))]
    (is (= [{:code 105
             :type "error"
             :colorized-message "A base can't have the same name as an interface or lib: mybase"
             :message           "A base can't have the same name as an interface or lib: mybase"
             :interfaces ["mybase"]
             :libs ["mybase"]
             :bases ["mybase"]}]
           (m105/errors interface-names libs bases color/none)))))
