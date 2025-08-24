module com.weatherom {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.weatherom to javafx.fxml;
    exports com.weatherom;
    exports com.weatherom.controllers;
    opens com.weatherom.controllers to javafx.fxml;
}