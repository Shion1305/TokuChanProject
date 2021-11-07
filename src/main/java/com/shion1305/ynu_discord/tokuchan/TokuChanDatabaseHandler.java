package com.shion1305.ynu_discord.tokuchan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TokuChanDatabaseHandler {
    static final String DB_URL = "jdbc:mysql://localhost/";
    static final String USER = "guest";
    static final String PASS = "guest123";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            Statement statement=conn.createStatement();
            statement.execute("CREATE IF NOT EXISTS DATABASE TokuChanData");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}