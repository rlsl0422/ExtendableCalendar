package com.example.extendablecalendar;

import java.sql.*;
import java.util.ArrayList;

public class ImportDateList {
    public static ArrayList<String> importdatelist(String time) throws ClassNotFoundException, SQLException {
        ArrayList<String> dates = new ArrayList<>();
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\PC\\IdeaProjects\\ExtendableCalendar\\DBMS\\Schedule_Table");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("Select date from SCHEDULE where date like '" + time + "%'");
        while (resultSet.next()) {
            dates.add(resultSet.getString("date").substring(6,8));
        }
        connection.close();
        return dates;
    }
}
