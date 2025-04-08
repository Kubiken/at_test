package com.example.demorestassured.controller;

import com.example.demorestassured.models.request.RunChecklistRequest;
import com.example.demorestassured.service.TestRunnerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AssuredController{

    private final TestRunnerService trs;

    @PostMapping(value = "/run", produces = "application/zip")
    public ResponseEntity<byte[]> runTests(@RequestBody RunChecklistRequest request) {
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"allure-results.zip\"")
                .body(trs.runTests(request));
    }
}
