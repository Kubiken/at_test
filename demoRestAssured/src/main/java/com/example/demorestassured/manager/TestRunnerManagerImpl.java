package com.example.demorestassured.manager;

import com.example.demorestassured.alluregen.AllureRepGenerator;
import com.example.demorestassured.models.request.RunChecklistRequest;
import com.example.demorestassured.models.response.testModels.ChecklistResult;
import com.example.demorestassured.models.response.testModels.StepResult;
import com.example.demorestassured.models.response.testModels.TestCaseResult;
import com.example.demorestassured.models.response.testModels.TestResponse;
import com.example.demorestassured.models.testModels.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestRunnerManagerImpl implements TestRunnerManager {
    @Override
    public ChecklistResult runTests(RunChecklistRequest request) {
        Checklist checklist = getChecklistById(request.getChecklistId());
        ChecklistResult result = new ChecklistResult();
        result.setName(request.getChecklistId().toString());
        result.setStart(Instant.now().getEpochSecond());

        result.setResults(checklist.getTestCases().stream()
                .map(testCase -> runTestCase(testCase, request.getHost(), request.getPort()))
                .collect(Collectors.toList()));

        result.setStop(Instant.now().getEpochSecond());
        AllureRepGenerator.generateAllureResults(result);
        return result;
    }

    private Checklist getChecklistById(Long checklistId) {
        Checklist checklist = new Checklist();
        TestCase testCase = new TestCase();
        Step step = new Step();
        Action action = new Action();
        ExpectedResponse er = new ExpectedResponse();
        Map<String, String> params = new HashMap<>();

        switch (checklistId.intValue()) {
            case 1:
                params.put("author", "tolkien");
                params.put("sort", "new");
                action.setQueryParameters(params);
                er.setHttpCode(200);
                er.setBody("test");
                break;
            case 2:
                params.put("code", "200");
                action.setPathVariables(params);
                er.setHttpCode(200);
                break;
        }

        step.setAction(action);
        step.setExpectedResponse(er);
        testCase.setSteps(Collections.singletonList(step));
        testCase.setId(checklistId.intValue() == 1 ? 1L : 2L);
        checklist.setTestCases(Collections.singletonList(testCase));

        return checklist;
    }

    private TestCaseResult runTestCase(TestCase testCase, String host, String port) {
        TestCaseResult tcr = new TestCaseResult();
        tcr.setStart(Instant.now().getEpochSecond());
        tcr.setStepResults(testCase.getSteps().stream()
                .map(testStep -> {
                    try {
                        return runTestStep(testStep, host, port);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList()));
        tcr.setTestCaseResult(tcr.getStepResults().stream()
                .noneMatch(stepResult -> stepResult.getStepResult().equals(false)));
        tcr.setStop(Instant.now().getEpochSecond());
        tcr.setTestCaseId(testCase.getId());
        return tcr;
    }


    private StepResult runTestStep(Step step, String host, String port) throws JSONException {
        StepResult sr = new StepResult();
        Action action = step.getAction();
        RequestSpecification httpRequest = buildRequest(action);

        sr.setStart(Instant.now().getEpochSecond());

        Response response = httpRequest
                .when()
                .get(buildLink(host, port))
                .then()
                .extract()
                .response();

        sr.setStop(Instant.now().getEpochSecond());
        validateResponse(response, step.getExpectedResponse(), sr);

        TestResponse tr = new TestResponse();
        tr.setBody(response.getBody().asString());
        Map<String, String> headers = response.getHeaders().asList().stream()
                        .collect(Collectors.toMap(
                                Header::getName,
                                Header::getValue
                        ));
        tr.setHeaders(headers);
        tr.setHttpCode(response.getStatusCode());
        sr.setTestResponse(tr);
        sr.setStepId(135L);
        return sr;
    }

    private RequestSpecification buildRequest(Action action) {
        RequestSpecification request = RestAssured.given();
        if (action.getHeaders() != null) {
            request.headers(action.getHeaders());
        }
        if (action.getBody() != null) {
            request.body(action.getBody());
        }
        if (action.getQueryParameters() != null) {
            request.queryParams(action.getQueryParameters());
        }
        if (action.getPathVariables() != null) {
            request.pathParams(action.getPathVariables());
        }

        return request;
    }

    private void validateResponse(Response response, ExpectedResponse expectedResponse, StepResult stepResult) {
        boolean isValid = true;
        String failMessage = "";
        if (expectedResponse.getBody() != null) {
            isValid = expectedResponse.getBody().equals(response.getBody().asString());
            if (!isValid) {
                failMessage = new StringBuilder().append("Expect body ")
                        .append(expectedResponse.getBody())
                        .append(" but get ")
                        .append(response.getBody().asString()).toString();
            }
        }
        if (expectedResponse.getHeaders() != null && isValid) {

        }
        if (expectedResponse.getHttpCode() != null && isValid) {
            isValid = expectedResponse.getHttpCode().equals(response.statusCode());
            if (!isValid) {
                failMessage = new StringBuilder().append("Expect response code ")
                        .append(expectedResponse.getHttpCode())
                        .append(" but get ")
                        .append(response.getStatusCode()).toString();
            }
        }
        stepResult.setStepResult(isValid);
        if (!isValid) {
            stepResult.setFailMessage(failMessage);
        }
    }

    private String buildLink(String host, String port) {
        StringBuilder stringBuilder =  new StringBuilder().append(host);
        if (port != null) {
            stringBuilder.append(":")
                    .append(port);
        }
        return stringBuilder.toString();
    }
}
