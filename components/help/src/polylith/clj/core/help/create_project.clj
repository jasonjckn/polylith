(ns ^:no-doc polylith.clj.core.help.create-project
  (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a project.\n"
       "\n"
       "  poly create project name:" (s/key "NAME" cm) " [" (s/key ":git-add" cm) "]\n"
       "    " (s/key "NAME" cm) " = The name of the project to create.\n"
       "\n"
       "    " (s/key ":git-add" cm) " = If " (s/key ":vcs" cm) " > " (s/key ":auto-add" cm) " in workspace.edn is set to false,\n"
       "               then we can pass in this flag instead to explicitly add the\n"
       "               created files to git.\n"
       "\n"
       "  Example:\n"
       "    poly create p name:myproject\n"
       "    poly create project name:myproject"))

(defn print-help [color-mode]
  (println (help-text color-mode)))

(comment
  (print-help "dark")
  #__)
