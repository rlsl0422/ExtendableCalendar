package com.example.extendablecalendar;

import java.sql.*;

public class SaveDatas {
    public static void saveDatas(String date,String title, String content) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\PC\\IdeaProjects\\ExtendableCalendar\\DBMS\\Schedule_Table");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select max(id) 'max' from SCHEDULE");
        int maxId = resultSet.getInt("max");
        maxId++;
        statement.execute("INSERT INTO SCHEDULE VALUES (" + String.valueOf(maxId) + ",'" + date + "', '"+ title +"','"+ content + "'"+ ")");
        connection.close();
    }
}
