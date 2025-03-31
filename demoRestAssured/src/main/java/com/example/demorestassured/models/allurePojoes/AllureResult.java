package com.example.demorestassured.models.allurePojoes;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AllureResult {
    private String uuid;
    private String historyId;
    private String testCaseId;
    private String testCaseName;
    private String fullName;
    private ArrayList<AllureLabel> labels;
    private ArrayList<Object> links;
    private String name;
    private String status;
    private String stage;
    private String description;
    private ArrayList<AllureStep> steps;
    private ArrayList<Object> attachments;
    private ArrayList<Object> parameters;
    private long start;
    private long stop;
}
