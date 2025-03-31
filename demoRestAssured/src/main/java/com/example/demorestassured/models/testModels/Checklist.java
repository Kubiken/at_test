package com.example.demorestassured.models.testModels;

import lombok.Data;

import java.util.List;

@Data
public class Checklist {
    private Long id;
    private Long contractId;
    private List<TestCase> testCases;
}
