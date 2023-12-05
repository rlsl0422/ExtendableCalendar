package com.example.extendablecalendar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DeleteDatas {
    public static void deletedatas(String id) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\PC\\IdeaProjects\\ExtendableCalendar\\DBMS\\Schedule_Table");
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM SCHEDULE WHERE id =" + id);
        connection.close();
    }
}
