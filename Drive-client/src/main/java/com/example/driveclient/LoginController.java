package com.example.driveclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    public void toSignInPage(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader signInLoader = new FXMLLoader(getClass().getResource("/sign-in.fxml"));
        stage.setScene(new Scene(signInLoader.load()));
        stage.show();
    }
}