-- :name create-palette! :! :n
-- :doc creates a new palette record
INSERT INTO palettes 
(color_0, color_1, color_2, color_3, url, url_id )
VALUES (:color_0, :color_1, :color_2, :color_3, :url, :url_id)

-- :name get-palette :? :1
-- :doc retrieve a palette given the id.
SELECT * FROM palletes 
WHERE id = :id

-- :name get-palette-by-url-id :? :1
-- :doc retrieve a palette given the url_id.
SELECT * FROM palettes 
WHERE url_id = :url_id

-- :name get-palettes :? :*
-- :doc selects all available palletes 
SELECT * FROM palettes 
