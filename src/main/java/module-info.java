module com.example.extendablecalendar {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;


    opens com.example.extendablecalendar to javafx.fxml;
    exports com.example.extendablecalendar;
}