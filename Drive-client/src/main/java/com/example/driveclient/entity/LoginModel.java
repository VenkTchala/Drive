package com.example.driveclient.entity;

import com.example.driveclient.dto.AuthRequest;
import com.example.driveclient.dto.LogInStatus;
import com.example.driveclient.service.UserService;
import com.example.driveclient.util.JsonMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class LoginModel {
    private final BooleanProperty logInState = new SimpleBooleanProperty();
    private final ObjectProperty<AuthRequest> loginDetails = new SimpleObjectProperty<>();
    private final BooleanProperty alertState = new SimpleBooleanProperty();
    private final StringProperty alertText = new SimpleStringProperty();

    @Getter(AccessLevel.NONE)
    private Service<Void> loginService = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return loginTask();
        }
    };

    @Getter(AccessLevel.NONE)
    private final Runnable errorAlert = () -> setAlertState(true,"Something went wrong, try again later");

    private Task<Void> loginTask (){


        Task<Void> loginTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try {
                    HttpResponse<JsonNode> response = UserService.logIn(loginDetails.get());
                    if (response.getStatus() != 200) {
                        Platform.runLater(errorAlert);
                    }
                    else {
                        LogInStatus logInStatus = JsonMapper.JsonToObj(response.getBody().toString(),LogInStatus.class);
                        if(!logInStatus.isSucess())
                            Platform.runLater(() -> setAlertState(true,"username or password is not valid!"));
                        else{
                            logInState.set(true);
                        }
                    }
                }
                catch (Exception e){
                    Platform.runLater(errorAlert);
                }
                return null;
            }
        };
        loginTask.setOnFailed(e -> Platform.runLater(errorAlert));

        return loginTask;
    }

    public void doLogin(){
        if(!loginService.isRunning()){
            loginService.reset();
            loginService.start();
        }
    }

    private void setAlertState (boolean state , String status ){
        alertState.set(state);
        alertText.set(status);
    }


}
