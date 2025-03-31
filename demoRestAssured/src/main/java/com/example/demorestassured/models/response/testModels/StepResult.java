package com.example.demorestassured.models.response.testModels;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class StepResult {

    private Long id;
    private Long start;
    private Long stop;
    private Long stepId;
    private Boolean stepResult;
    private String failMessage;
    private TestResponse testResponse;
}
