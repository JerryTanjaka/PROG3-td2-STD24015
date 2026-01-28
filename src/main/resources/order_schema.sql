-- Table des commandes
CREATE TABLE "order" (
                         id SERIAL PRIMARY KEY,
                         reference VARCHAR(10) UNIQUE NOT NULL, -- Format ORDXXXXX
                         creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table de jointure pour les plats dans une commande
CREATE TABLE dish_order (
                            id SERIAL PRIMARY KEY,
                            id_order INT NOT NULL REFERENCES "order"(id),
                            id_dish INT NOT NULL REFERENCES dish(id),
                            quantity INT NOT NULL CHECK (quantity > 0)
);