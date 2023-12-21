package com.example.extendablecalendar;


import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;

public class ImportDatas {
    public static ArrayList<ArrayList<String>> importdatas(String date) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:DBMS/Schedule_Table");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT id ,title, content FROM SCHEDULE where date = " + date);
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        int cnt = 0;
        while (resultSet.next()) {
            data.add(new ArrayList<>(3));
            data.get(cnt).add(resultSet.getString("id"));
            data.get(cnt).add(resultSet.getString("title"));
            data.get(cnt).add(resultSet.getString("content"));
            cnt++;
        }
        connection.close();
        return data;
    }
}
