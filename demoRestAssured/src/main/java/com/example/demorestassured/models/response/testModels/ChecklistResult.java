package com.example.demorestassured.models.response.testModels;

import lombok.Data;

import java.util.List;

@Data
public class ChecklistResult {
    private Long start;
    private Long stop;
    private List<TestCaseResult> results;
    private String name;
}
