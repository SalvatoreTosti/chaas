(ns chaas.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [chaas.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[chaas started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[chaas has shut down successfully]=-"))
   :middleware wrap-dev})
