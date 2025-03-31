package com.example.demorestassured.models.testModels;

import lombok.Data;

import java.util.List;

@Data
public class TestCase {
    private Long id;
    private String summary;
    private String operationId;
    private String preconditionText;
    private List<Step> steps;
}
