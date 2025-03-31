package com.example.demorestassured.models.testModels;

import lombok.Data;

@Data
public class Step {
    private Integer id;
    private Long order;
    private String name;
    private Action action;
    private ExpectedResponse expectedResponse;
}
