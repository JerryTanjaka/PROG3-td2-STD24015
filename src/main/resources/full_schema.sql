-- Types énumérés
CREATE TYPE category_type AS ENUM ('VEGETABLE','ANIMAL','MARINE','DAIRY','OTHER');
CREATE TYPE dish_type AS ENUM ('STARTER','MAIN','DESSERT');
CREATE TYPE unit_type AS ENUM ('KG', 'L', 'PCS');
CREATE TYPE mouvement_type AS ENUM ('IN', 'OUT');

-- Tables principales
CREATE TABLE dish (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      dish_type dish_type NOT NULL,
                      selling_price NUMERIC(10,2) -- nullable selon sujet
);

CREATE TABLE ingredient (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            price NUMERIC(10,2) NOT NULL,
                            category category_type NOT NULL
);

-- Relation Many-to-Many entre Dish et Ingredient
CREATE TABLE dish_ingredient (
                                 id SERIAL PRIMARY KEY,
                                 id_dish INT NOT NULL REFERENCES dish(id) ON DELETE CASCADE,
                                 id_ingredient INT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
                                 quantity_required NUMERIC(10,2) NOT NULL,
                                 unit unit_type NOT NULL,
                                 UNIQUE(id_dish, id_ingredient)
);

-- Historique des mouvements de stock
CREATE TABLE stock_movement (
                                id SERIAL PRIMARY KEY,
                                id_ingredient INT NOT NULL REFERENCES ingredient(id),
                                quantity NUMERIC(10,2) NOT NULL,
                                type mouvement_type NOT NULL,
                                unit unit_type NOT NULL,
                                creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Système de Commandes (Annexe 2)
CREATE TABLE "order" (
                         id SERIAL PRIMARY KEY,
                         reference VARCHAR(10) UNIQUE NOT NULL, -- Format ORDXXXXX
                         creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dish_order (
                            id SERIAL PRIMARY KEY,
                            id_order INT NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
                            id_dish INT NOT NULL REFERENCES dish(id),
                            quantity INT NOT NULL CHECK (quantity > 0)
);