package com.example.driveclient;

import com.example.driveclient.dto.UserSignIn;
import com.example.driveclient.entity.SiginModel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignInController implements Initializable {
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField  email;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField reTypePass;
    @FXML
    private AnchorPane pane;

    private StringProperty errorMessage = new SimpleStringProperty();

    private SiginModel siginModel = new SiginModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessage.bind(siginModel.getAlertText());

        siginModel.getAlertState().addListener((obs,old,newVal) -> {
            if( newVal != null && newVal)
                Platform.runLater(() ->
                    showError(Alert.AlertType.ERROR,pane.getScene().getWindow(),"Sign-in failed", errorMessage.get(),false));
        });

        siginModel.getSigninState().addListener((obs,old,newVal) -> {
            if( newVal != null && newVal)
                Platform.runLater(() -> showError(Alert.AlertType.CONFIRMATION , pane.getScene().getWindow(),"Sign-in sucess" , "Sign in with : " + email.getText()  + " Sucess" , true )); });

    }

    public void toLogInPage(Event event) {
//        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            Stage stage = DriveApplication.getMainStage();
            FXMLLoader signInLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            stage.setScene(new Scene(signInLoader.load()));
            stage.show();
        }
        catch (IOException e){
        }
    }


    private void showError(Alert.AlertType alertType, Window owner , String title,String message,boolean redirect){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.setOnCloseRequest(e -> Platform.runLater(() -> siginModel.getAlertState().set(false)));

        if(redirect)
            alert.setOnCloseRequest(e -> Platform.runLater(() -> toLogInPage(e)
            ));

        alert.show();
    }

    public void doSignIn(){
        Platform.runLater(() -> siginModel.getDetails().set(getSignInDetails()));
        siginModel.doSignIn();
    }

    private UserSignIn getSignInDetails(){

        String fName = firstName.getText();
        String lName = lastName.getText();
        String emailText = email.getText();
        String passwordText = passwordField.getText();

        return
                UserSignIn.builder()
                        .firstName(fName)
                        .lastName(lName)
                        .password(passwordText)
                        .email(emailText)
                        .build();
    }
}
