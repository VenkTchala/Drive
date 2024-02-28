package com.example.driveclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController {
    public void toLogInPage(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader signInLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        stage.setScene(new Scene(signInLoader.load()));
        stage.show();
    }
}
