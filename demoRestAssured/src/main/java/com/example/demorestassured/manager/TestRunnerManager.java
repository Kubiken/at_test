package com.example.demorestassured.manager;

import com.example.demorestassured.models.request.RunChecklistRequest;
import com.example.demorestassured.models.response.testModels.ChecklistResult;
import com.example.demorestassured.models.response.testModels.TestCaseResult;

import java.util.List;

public interface TestRunnerManager {
    ChecklistResult runTests(RunChecklistRequest request);
}
