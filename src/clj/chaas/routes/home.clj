(ns chaas.routes.home
  (:require
   [chaas.layout :as layout]
   [clojure.java.io :as io]
   [chaas.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [ctmx.core :as ctmx]
   [ctmx.render :as render]
   [hiccup.page :refer [html5]]
   [chaas.models.core :as models]))

(defn html-response
  [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})


(defn html5-response
  ([body]
   (html-response
     (html5
       [:head
        [:meta {:charset "UTF-8"}]
        [:meta {:name "viewport"
                :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]]
       [:body (render/walk-attrs body)]
       [:script {:src "https://unpkg.com/htmx.org@1.5.0"}]
       [:link {:href "https://unpkg.com/tailwindcss@^2/dist/tailwind.min.css" :rel "stylesheet"}]
       ))))


(defn link
  [text destination]
  [:a {:href destination :target "_" :class "text-blue-500 hover:opacity-75"} text])

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(ctmx/defcomponent ^:endpoint fetch [req input]
  (let [{:keys [color_0 color_1 color_2 color_3 result] :as x} (models/fetch-palette input)
        color-0 (str "background-color: #" color_0)
        color-1 (str "background-color: #" color_1)
        color-2 (str "background-color: #" color_2)
        color-3 (str "background-color: #" color_3)] 

    [:div 
     {:hx-target "this" :hx-swap "outerHTML" :class "grid justify-center items-center grid-flow-col grid-cols-2 gap-5" }
     [:div
      [:label {:class "font-bold text-blue-500 text-center text-4xl"} "Color Hunt Palette ID"]
      [:input
       {:name "input"
        :autocomplete "off"
        :value input 
        :hx-get "fetch"
        :class "form-textarea w-full border-blue-500 border rounded"
        :placeholder ""}]]
     (if (= :success result) 
       [:div {:class "grid justify-center items-center grid-cols-4 gap-5"}
        [:div {:class "w-32 h-32 rounded m-auto" :style color-0}]
        [:div {:class "w-32 h-32 rounded m-auto" :style color-1}]
        [:div {:class "w-32 h-32 rounded m-auto" :style color-2}]
        [:div {:class "w-32 h-32 rounded m-auto" :style color-3}]]
       [:div {:class "text-4xl"} "No palette found with that id."])]))

(defn home-routes
  []
  (ctmx/make-routes
    "/"
    (fn [req]
      (html5-response
        [:div {:class "container mx-auto p-4"}
         [:div {:class "font-bold text-blue-500 text-center my-5 text-5xl"} "chaas"]
         (fetch req (rand-nth ["f5e8c7deba9d9e77776f4c5b" "bc658d82c4c3f9d89cf5a7a7" "6f69ac95dac1ffeba1fd6f96"]))]))))
