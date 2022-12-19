module com.example.hest {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires java.desktop;

    opens com.example.hest to javafx.fxml;
    exports com.example.hest;
    exports com.example.hest.model;
    opens com.example.hest.model to javafx.fxml;
    exports com.example.hest.dao;
    opens com.example.hest.dao to javafx.fxml;
}