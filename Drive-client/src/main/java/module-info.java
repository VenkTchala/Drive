module com.example.driveclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    //needed for HTTP
    requires unirest.java;

    //needed for JSON
    requires gson;
    requires java.sql;

    requires lombok;

    opens com.example.driveclient to javafx.fxml;
    exports com.example.driveclient;
    exports com.example.driveclient.dto;

//    opens com.edencoding.models.openVision to gson;
//    opens com.edencoding.models.dogs to gson;
    opens  com.example.driveclient.dto to gson;
}