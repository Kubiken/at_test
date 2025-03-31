package com.example.demorestassured.controller;

import com.example.demorestassured.manager.TestRunnerManager;
import com.example.demorestassured.models.request.RunChecklistRequest;
import com.example.demorestassured.models.response.testModels.ChecklistResult;
import com.example.demorestassured.models.response.testModels.TestCaseResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class AssuredController{

    private final TestRunnerManager trm;

    @PostMapping("/run")
    public ChecklistResult runTests(@RequestBody RunChecklistRequest request) {
        return trm.runTests(request);
    }
}
