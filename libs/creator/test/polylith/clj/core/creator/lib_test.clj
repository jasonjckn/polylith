(ns polylith.clj.core.creator.lib-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.creator.brick :as brick]
            [polylith.clj.core.test-helper.interface :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-lib--with-missing-name--return-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "c" "name:"))]
    (is (= "  A brick name must be given.\n"
           output))))

(deftest create-lib--when-lib-already-exists--return-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "c" "name:my-lib")
                 (helper/execute-command "ws1" "create" "lib" "name:my-lib"))]
    (is (= (str brick/create-brick-message "\n"
                "  The brick 'my-lib' already exists.\n")
           output))))

(deftest create-lib--without-giving-an-interface--performs-expected-actions
  (let [src-ifc-dir "ws1/libs/my-lib/src/se/example/my_lib"
        test-ifc-dir "ws1/libs/my-lib/test/se/example/my_lib"
        output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "c" "name:my-lib"))]
    (is (= (str brick/create-brick-message "\n")
           output))

    (is (= #{".gitignore"
             ".vscode"
             ".vscode/settings.json"
             "bases"
             "bases/.keep"
             "libs"
             "libs/.keep"
             "libs/my-lib"
             "libs/my-lib/deps.edn"
             "libs/my-lib/resources"
             "libs/my-lib/resources/my-lib"
             "libs/my-lib/resources/my-lib/.keep"
             "libs/my-lib/src"
             "libs/my-lib/src/se"
             "libs/my-lib/src/se/example"
             "libs/my-lib/src/se/example/my_lib"
             "libs/my-lib/src/se/example/my_lib/interface.clj"
             "libs/my-lib/test"
             "libs/my-lib/test/se"
             "libs/my-lib/test/se/example"
             "libs/my-lib/test/se/example/my_lib"
             "libs/my-lib/test/se/example/my_lib/interface_test.clj"
             "deps.edn"
             "development"
             "development/src"
             "development/src/.keep"
             "logo.png"
             "projects"
             "projects/.keep"
             "readme.md"
             "workspace.edn"}
           (helper/paths "ws1")))

    (is (= ["(ns se.example.my-lib.interface)"]
           (helper/content src-ifc-dir "interface.clj")))

    (is (= ["(ns se.example.my-lib.interface-test"
            "  (:require [clojure.test :as test :refer :all]"
            "            [se.example.my-lib.interface :as my-lib]))"
            ""
            "(deftest dummy-test"
            "  (is (= 1 1)))"]
           (helper/content test-ifc-dir "interface_test.clj")))))

(deftest create-lib--without-with-a-different-interface--performs-expected-actions
  (let [src-ifc-dir "ws1/libs/my-lib/src/se/example/my_interface"
        test-ifc-dir "ws1/libs/my-lib/test/se/example/my_interface"
        output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "c" "name:my-lib" "interface:my-interface"))]
    (is (= (str brick/create-brick-message "\n")
           output))

    (is (= #{".gitignore"
             ".vscode"
             ".vscode/settings.json"
             "bases"
             "bases/.keep"
             "libs"
             "libs/.keep"
             "libs/my-lib"
             "libs/my-lib/deps.edn"
             "libs/my-lib/resources"
             "libs/my-lib/resources/my-lib"
             "libs/my-lib/resources/my-lib/.keep"
             "libs/my-lib/src"
             "libs/my-lib/src/se"
             "libs/my-lib/src/se/example"
             "libs/my-lib/src/se/example/my_interface"
             "libs/my-lib/src/se/example/my_interface/interface.clj"
             "libs/my-lib/test"
             "libs/my-lib/test/se"
             "libs/my-lib/test/se/example"
             "libs/my-lib/test/se/example/my_interface"
             "libs/my-lib/test/se/example/my_interface/interface_test.clj"
             "deps.edn"
             "development"
             "development/src"
             "development/src/.keep"
             "logo.png"
             "projects"
             "projects/.keep"
             "readme.md"
             "workspace.edn"}
           (helper/paths "ws1")))

    (is (= ["(ns se.example.my-interface.interface)"]
           (helper/content src-ifc-dir "interface.clj")))

    (is (= ["(ns se.example.my-interface.interface-test"
            "  (:require [clojure.test :as test :refer :all]"
            "            [se.example.my-interface.interface :as my-interface]))"
            ""
            "(deftest dummy-test"
            "  (is (= 1 1)))"]
           (helper/content test-ifc-dir "interface_test.clj")))))
