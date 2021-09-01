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
       [:link {:href "https://unpkg.com/tailwindcss@^2/dist/tailwind.min.css" :rel "stylesheet"}]))))

(defn nav-bar []
  [:nav {:class "relative flex flex-wrap items-center justify-between px-2 py-3 bg-blue-500 mb-3"}
   [:div {:class "container px-4 mx-auto flex flex-wrap items-center justify-between"}
    [:div {:class "relative flex justify-between w-auto px-4 static block justify-start"}
     [:a {:class "text-sm font-bold leading-relaxed inline-block mr-4 py-2 whitespace-nowrap uppercase text-white hover:opacity-75" :href "#"}
      "Home"]
     [:a {:class "text-sm font-bold leading-relaxed inline-block mr-4 py-2 whitespace-nowrap uppercase text-white hover:opacity-75" :href "/api/api-docs/"}
      "API"]
     [:a {:class "text-sm font-bold leading-relaxed inline-block mr-4 py-2 whitespace-nowrap uppercase text-white hover:opacity-75" :href "https://www.colorhunt.co/"}
      "colorhunt.co"]]
    [:div {:class "flex flex-grow items-center"}
     [:ul {:class "flex flex-row list-none ml-auto"}
      [:li {:class "nav-item"}
       [:a {:class "px-3 py-2 flex items-center text-xs uppercase font-bold leading-snug text-white hover:opacity-75"
            :href "https://github.com/SalvatoreTosti"
            :target "_"}
        [:i {:class "text-lg leading-lg text-white opacity-75"} "Salvatore"]]]]]]])

(defn link
  [text destination]
  [:a {:href destination :target "_" :class "text-blue-500 hover:opacity-75"} text])

(ctmx/defcomponent ^:endpoint palette [req input]
  (let [{:keys [color_0 color_1 color_2 color_3 result] :as x} (models/fetch-palette input)
        color-0 (str "background-color: #" color_0)
        color-1 (str "background-color: #" color_1)
        color-2 (str "background-color: #" color_2)
        color-3 (str "background-color: #" color_3)] 
    [:div 
     {:hx-target "this" :hx-swap "outerHTML" :class "grid justify-center items-center grid-flow-col grid-cols-2 gap-5" }
     [:div
      [:input
       {:name "input"
        :autocomplete "off"
        :value input 
        :hx-get "palette"
        :class "form-textarea w-full border-blue-500 border rounded"
        :placeholder "Color Hunt palette URL or ID"}]]
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
        [:div (nav-bar)
        [:div {:class "container mx-auto p-4"}
         [:div {:class "font-bold text-blue-500 text-center my-5 text-5xl"} "Color Hunt As A Service"]
         (palette req (rand-nth ["f5e8c7deba9d9e77776f4c5b" "bc658d82c4c3f9d89cf5a7a7" "6f69ac95dac1ffeba1fd6f96"]))]]))))
