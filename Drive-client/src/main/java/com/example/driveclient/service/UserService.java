package com.example.driveclient.service;

import com.example.driveclient.dto.AuthRequest;
import com.example.driveclient.dto.UserSignIn;
import com.example.driveclient.util.JsonMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class UserService {
    public static HttpResponse<JsonNode> signIn(UserSignIn userSignIn) throws UnirestException {

        final String body = JsonMapper.getJson(userSignIn);

             return  Unirest.post("http://localhost:8080/auth/register")
                     .header("Content-Type", "application/json")
                     .body(body)
                     .asJson();
    }

    public static HttpResponse<JsonNode> logIn (AuthRequest authRequest) throws UnirestException{
        final String body = JsonMapper.getJson(authRequest);

        return Unirest.post("http://localhost:8080/auth/token")
                .header("Content-Type", "application/json")
                .body(body)
                .asJson();
    }

}
