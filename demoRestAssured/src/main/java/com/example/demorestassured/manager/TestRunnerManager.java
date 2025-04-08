package com.example.demorestassured.manager;

import com.example.demorestassured.models.request.RunChecklistRequest;

import java.util.List;

public interface TestRunnerManager {
    void runTests(RunChecklistRequest request);
}
