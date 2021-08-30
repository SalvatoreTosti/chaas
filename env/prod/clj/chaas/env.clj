(ns chaas.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[chaas started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[chaas has shut down successfully]=-"))
   :middleware identity})
