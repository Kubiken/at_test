package com.example.demorestassured.models.testModels;

import lombok.Data;

import java.util.Map;

@Data
public class ExpectedResponse {
    private Integer httpCode;
    private String description;
    private Map<String, String> headers;
    private String body;
}
