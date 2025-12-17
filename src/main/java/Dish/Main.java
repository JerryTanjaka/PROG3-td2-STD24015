package Dish;

import Dish.Util.DBConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBConnection dbConnection  = new DBConnection();
        dbConnection.getDBConnection();
    }
}
