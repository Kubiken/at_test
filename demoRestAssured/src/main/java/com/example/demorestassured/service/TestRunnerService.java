package com.example.demorestassured.service;

import com.example.demorestassured.models.request.RunChecklistRequest;

public interface TestRunnerService {
    byte[] runTests (RunChecklistRequest request);
}
