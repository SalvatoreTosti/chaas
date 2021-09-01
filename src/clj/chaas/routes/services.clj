(ns chaas.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [chaas.middleware.formats :as formats]
    [ring.util.http-response :refer :all]
    [clojure.java.io :as io]
    [net.cgrand.enlive-html :as html]
    [clojure.string :as str]
    [chaas.models.core :as models]
    [chaas.db.core :as db]))

(defn save-palette! [c0 c1 c2 c3 url url_id]
  (db/create-palette!
      {:color_0 c0
       :color_1 c1
       :color_2 c2
       :color_3 c3
       :url url
       :url_id url_id}))

(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 coercion/coerce-exceptions-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/palettes"
     {:post {:summary "plus with spec body parameters"
             :parameters {:body {:target string?}}
             :responses {200 {:body {:color_0 string? 
                                     :color_1 string? 
                                     :color_2 string?
                                     :color_3 string?
                                     :url string?
                                     :url_id string?}}}
             :handler (fn [{{{:keys [target]} :body} :parameters}]
                        (let [{:keys [result] :as x} (models/fetch-palette target)]
                          (if (= :success result)
                            {:status 200 :body x}
                            {:status 400})))}}]])
