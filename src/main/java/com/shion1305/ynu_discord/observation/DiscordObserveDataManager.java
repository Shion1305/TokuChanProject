package com.shion1305.ynu_discord.observation;

import java.sql.Connection;

public class DiscordObserveDataManager {
    Connection conn;
    private static final String PARAM_INTERNAL_ID = "INTERNAL_ID";
    private static final String PARAM_USERNAME = "USERNAME";
    private static final String PARAM_USERID ="USERID";
    private static final String PARAM_CHANNELS ="CHANNELS";
    String tableNameActivity="DiscordActivityLog";
    String tableNameProfile ="DiscordProfile";
    DiscordObserveDataManager(){

//        File file = new File("databases/DiscordActivity.db");
//        String url = "jdbc:sqlite:" + file.getAbsolutePath();
//        try {
//            conn = DriverManager.getConnection(url);
//            DatabaseMetaData meta = conn.getMetaData();
//            System.out.println("The driver name is " + meta.getDriverName());
//            System.out.println("A new database has been created.");
//            Statement statement = conn.createStatement();
//            String sql = "CREATE TABLE IF NOT EXISTS " + tableNameProfile + " (\n"
//                    + PARAM_INTERNAL_ID+"	 integer PRIMARY KEY,\n"
//                    + PARAM_USERID+" integer NOT NULL,\n"
//                    + PARAM_USERNAME + " text NOT NULL,\n"
//                    + PARAM_CHANNELS+"text"
//                    + ");";
//            statement.execute(sql);
//            statement.close();
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
    }

    public static void log(int eventType,Object[] params){

    }

}
