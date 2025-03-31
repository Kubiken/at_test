package com.example.demorestassured.models.response.testModels;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.HashCodeExclude;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestCaseResult {
    private Long id;
    private Long start;
    private Long stop;
    private Long testCaseId;
    private Boolean testCaseResult;
    private List<StepResult> stepResults;
}
