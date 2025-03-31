package com.example.demorestassured.models.testModels;

import lombok.Data;

import java.util.Map;

@Data
public class Action {
    private Map<String, String> pathVariables;
    private Map<String, String> queryParameters;
    private Map<String, String> headers;
    private String body;
}
