package com.example.demorestassured.models.response.testModels;

import lombok.Data;

import java.util.Map;
@Data
public class TestResponse {
    private Integer httpCode;
    private String description;
    private Map <String, String> headers;
    private String body;
}
