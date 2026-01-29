CREATE DATABASE mini_dish_db;

--connexion au db
\c mini_dish_db;

-- Création de l'utilisateur
CREATE USER mini_dish_db_manager WITH PASSWORD '123456';

--ajout privièges connection au db
GRANT CONNECT ON DATABASE mini_dish_db TO mini_dish_db_manager;

--ajout privilèges créations table
GRANT CREATE ON DATABASE mini_dish_db TO mini_dish_db_manager;

-- Attribution des privilèges à l'utilisateur
GRANT select,update,insert,delete  ON dish TO mini_dish_db_manager;
GRANT select,update,insert,delete  ON ingredient TO mini_dish_db_manager;
GRANT select,update,insert,delete  ON dish_ingredient TO mini_dish_db_manager;
GRANT select,update,insert,delete  ON stock_movement TO mini_dish_db_manager;
GRANT select,update,insert,delete  ON "order" TO mini_dish_db_manager;
GRANT select,update,insert,delete  ON dish_order TO mini_dish_db_manager;
GRANT select,update,insert,delete  ON restaurant_table TO mini_dish_db_manager;

GRANT USAGE, SELECT, UPDATE ON SEQUENCE ingredient_id_seq TO mini_dish_db_manager;
GRANT USAGE, SELECT ON SEQUENCE dish_id_seq TO mini_dish_db_manager;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE order_id_seq TO mini_dish_db_manager;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE dish_order_id_seq TO mini_dish_db_manager;



