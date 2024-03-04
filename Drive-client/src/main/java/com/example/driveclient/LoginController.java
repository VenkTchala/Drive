package com.example.driveclient;

import com.example.driveclient.dto.AuthRequest;
import com.example.driveclient.entity.LoginModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.FontIconTableCell;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField email;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Pane pane;

    private final LoginModel loginModel = new LoginModel();

    private final StringProperty errorMessage = new SimpleStringProperty();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessage.bind(loginModel.getAlertText());

        loginModel.getAlertState().addListener((obs,old,newval) -> {
            if(newval != null && newval)
                Platform.runLater(() -> showError(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Login error" , errorMessage.get(),false) );
        });
        loginModel.getLogInState().addListener((obs,old,newval) -> {
            if(newval != null && newval)
                Platform.runLater(this::tofileManager);
        });
    }

    public void toSignInPage(ActionEvent event) throws IOException {
        Stage stage = DriveApplication.getMainStage();
        FXMLLoader signInLoader = new FXMLLoader(getClass().getResource("/sign-in.fxml"));
        stage.setScene(new Scene(signInLoader.load()));
        stage.show();
    }

    public void tofileManager(){
        try {
            Stage stage = DriveApplication.getMainStage();
            FXMLLoader signInLoader = new FXMLLoader(getClass().getResource("/file-Manager.fxml"));
            Scene scene = new Scene(signInLoader.load());
            scene.getStylesheets().add("/Styles.css");
            stage.setScene(scene);
            stage.show();
            }
        catch (IOException e){
            FontIcon icon = new FontIcon("fas-folder");
            FontIconTableCell<String , String> iconTableCell = new FontIconTableCell<>();
            iconTableCell.setGraphic(icon);
            iconTableCell.setText("show");
        }
    }

    public void doLogin(){
        loginModel.getLoginDetails().set(
                AuthRequest
                        .builder()
                        .username(email.getText())
                        .password(passwordField.getText())
                        .build()
        );
        loginModel.doLogin();
    }


    private void showError(Alert.AlertType alertType, Window owner , String title, String message, boolean redirect){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.setOnCloseRequest(e -> Platform.runLater(() -> loginModel.getAlertState().set(false)));

//        if(redirect)
//            alert.setOnCloseRequest(e -> Platform.runLater(() -> toLogInPage(e)
//            ));

        alert.show();
    }

}