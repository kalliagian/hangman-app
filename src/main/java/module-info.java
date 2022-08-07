module com.example.apphangman {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;


    opens app.apphangman to javafx.fxml;
    exports app.apphangman;
}