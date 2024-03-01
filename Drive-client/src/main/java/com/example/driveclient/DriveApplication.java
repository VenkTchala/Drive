package com.example.driveclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
import java.util.Map;

public class DriveApplication extends Application {

    private Stage primaryStage;

    @Getter
    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        DriveApplication.mainStage = stage;

        loadScene();
        stage.setTitle("Drive");
        stage.show();
    }

    public void loadScene() throws IOException{
        FXMLLoader loader = new FXMLLoader(DriveApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}