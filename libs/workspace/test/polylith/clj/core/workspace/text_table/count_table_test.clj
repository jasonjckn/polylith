(ns polylith.clj.core.workspace.text-table.count-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.text-table.number-of-entities :as count-table]))

(def workspace {:settings {:color-mode "none"
                           :thousand-separator ","}
                :projects (repeat 2 nil)
                :bases (repeat 3 nil)
                :libs (repeat 15 nil)
                :interfaces (repeat 14 nil)})

(deftest table--interfaces-libs-bases-and-projects--returns-correct-list
  (is (= ["  projects: 2   interfaces: 14"
          "  bases:    3   libs: 15"]
         (count-table/table workspace))))
