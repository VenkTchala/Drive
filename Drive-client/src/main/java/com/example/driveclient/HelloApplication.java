package com.example.driveclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class HelloApplication extends Application {

    private Stage primaryStage;

    private final Map<String, FXMLLoader> sceneMap = Map.of(
            "login", new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"))
    );


    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        changeScene("login");
        stage.setTitle("Drive");
//        stage.setScene(scene);
        stage.show();
    }

    public void changeScene(String fxml) throws IOException{
        Scene scene = new Scene(sceneMap.get(fxml).load());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}