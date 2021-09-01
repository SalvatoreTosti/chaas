(ns chaas.models.core
  (:require
    [chaas.db.core :as db]
    [clojure.string :as str]
    [net.cgrand.enlive-html :as html]))

(defn save-palette! [c0 c1 c2 c3 url url_id]
  (db/create-palette!
      {:color_0 c0
       :color_1 c1
       :color_2 c2
       :color_3 c3
       :url url
       :url_id url_id}))


(defn get-url [id-or-url]
  (if (str/includes? id-or-url "colorhunt.co/palette/")
    id-or-url
    (str "https://www.colorhunt.co/palette/" id-or-url)))

(defn fetch-palette [target-id]
  (if-let [palette (db/get-palette-by-url-id {:url_id target-id})]
    (assoc palette :result :success)
    (let [url (get-url target-id)
          page (with-open [inputstream (-> (java.net.URL. url)
                                           .openConnection
                                           (doto (.setRequestProperty "User-Agent"
                                                                      "Mozilla/5.0 ..."))
                                           .getContent)]
                 (html/html-resource inputstream))
          palettes (nth (html/select page [:meta]) 7)
          [c0 c1 c2 c3] (->> #" "
                             (str/split (:content (:attrs palettes)))
                             (drop 2)
                             (take 4)
                             (map #(str/join (drop 1 %))))]
      (if (and 
            (not (str/includes? url " "))
            (reduce (fn [x y] (and x y)) (map #(not (or (nil? %) (str/blank? %))) [c0 c1 c2 c3])))
        (do 
          (save-palette! c0 c1 c2 c3 url target-id)
          {:color_0 c0
           :color_1 c1
           :color_2 c2
           :color_3 c3
           :url url
           :url_id target-id
           :result :success})
        {:result :failure}))))
