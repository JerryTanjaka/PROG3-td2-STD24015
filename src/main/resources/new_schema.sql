create type unit_type as enum ('KG', 'L', 'PCS');


create table dish_ingredient
(
    id                serial primary key,
    id_dish           int            not null,
    id_ingredient     int            not null,
    quantity_required numeric(10, 2) not null,
    unit              unit_type      not null,

constraint fk_dish foreign key (id_dish)
    references Dish (id)
    on delete cascade,
constraint fk_ingredient
    foreign key (id_ingredient)
        references Ingredient (id)
    on delete cascade,
    constraint unique_dish_ingredient
    unique (id_dish, id_ingredient)
);

-- rename price to selling price
-- ALTER TABLE Dish RENAME COLUMN price TO selling_price;

-- add selling_price if it doesnt
ALTER TABLE Dish ADD COLUMN IF NOT EXISTS selling_price NUMERIC;

-- delete id_dish column in ingredient since it's not necessary anymore
ALTER TABLE ingredient DROP COLUMN IF EXISTS id_dish;

-- 1. Nouveau type pour différencier Entrée et Sortie
CREATE TYPE mouvement_type AS ENUM ('IN', 'OUT');

-- 2. Table des mouvements
CREATE TABLE StockMovement (
                               id SERIAL PRIMARY KEY,
                               id_ingredient INT NOT NULL,
                               quantity NUMERIC(10, 2) NOT NULL,
                               type mouvement_type NOT NULL,
                               unit unit_type NOT NULL, -- Utilise le type unit_type que tu as déjà (KG, L, PCS)
                               creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_ingredient_stock FOREIGN KEY (id_ingredient) REFERENCES Ingredient(id)
);