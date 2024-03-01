package com.example.driveclient.entity;

import com.example.driveclient.dto.SignInStatus;
import com.example.driveclient.dto.UserSignIn;
import com.example.driveclient.service.UserService;
import com.example.driveclient.util.JsonMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class SiginModel {
    private final ObjectProperty<Boolean> signinState = new SimpleObjectProperty<>(false);

    private final ObjectProperty<Boolean> alertState = new SimpleObjectProperty<>(false);
    private final StringProperty alertText = new SimpleStringProperty();
//    private final

    private final ObjectProperty<UserSignIn> details = new SimpleObjectProperty<>(null);

    @Getter(AccessLevel.NONE)
    private final Service<Void> signInService = new Service<>() {
        @Override
        protected Task<Void> createTask() {
            return signInTask();
        }
    };


    private Task<Void> signInTask(){
        Task<Void> signInTask = new Task<>() {
            @Override
            protected Void call() throws Exception {

                try {
                    HttpResponse<JsonNode> response =
                            UserService.signIn(details.get());

                    if(response.getStatus() != 200)
                        Platform.runLater(() -> setAlertState(true, "Something went wrong, try again later"));
                    else {
                        SignInStatus reply = JsonMapper.JsonToObj(response.getBody().toString(),SignInStatus.class);
                        boolean sucess = reply.isSucess();
                        if(!sucess)
                            Platform.runLater(() -> setAlertState(true, reply.getErrorMessage()));
                        else signinState.set(true);
                    }
                }
                catch (UnirestException e){
                    Platform.runLater(() -> setAlertState(true , "Something went wrong, try again later" ));
                }
                return null;
            }
        };

        signInTask.setOnFailed(e -> setAlertState(true , "Something went wrong, try again later" )

        );

        return signInTask;
    }


    public void doSignIn() {
        if (!signInService.isRunning()) {
            signInService.reset();
            signInService.start();
        }
    }


    private void setAlertState(boolean state , String text ){
        alertState.set(state);
        alertText.set(text);
    }

    private void setSigninState(boolean val){
        signinState.set(val);
    }

}
