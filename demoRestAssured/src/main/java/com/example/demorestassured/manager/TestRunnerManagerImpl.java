package com.example.demorestassured.manager;

import com.example.demorestassured.models.request.RunChecklistRequest;
import com.example.demorestassured.models.testModels.*;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demorestassured.manager.TextTemplates.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestRunnerManagerImpl implements TestRunnerManager {

    @Override
    public void runTests(RunChecklistRequest request) {
        Checklist checklist = getChecklistById(request.getChecklistId());

        checklist.getTestCases().stream()
                .forEach(testCase -> runTestCase(testCase, request.getHost(), request.getPort()));
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

    private void runTestCase(TestCase testCase, String host, String port) {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        String testCaseUUId = UUID.randomUUID().toString();
        TestResult allureTestResult = new TestResult();
        allureTestResult.setUuid(testCaseUUId);
        allureTestResult.setFullName(testCase.getId().toString());
        lifecycle.scheduleTestCase(allureTestResult);
        lifecycle.startTestCase(testCaseUUId);

        Status status = testCase.getSteps().stream()
                .map(testStep -> {
                    try {
                        return runTestStep(testStep, host, port, testCaseUUId);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .allMatch(stepResult -> stepResult) ? Status.PASSED : Status.FAILED;


        allureTestResult.setStatus(status);

        lifecycle.stopTestCase(testCaseUUId);
        lifecycle.writeTestCase(testCaseUUId);
    }


    private boolean runTestStep(Step step, String host, String port, String testCaseUUID) throws JSONException {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        String stepUUId = UUID.randomUUID().toString();
        io.qameta.allure.model.StepResult allureStepResult = new io.qameta.allure.model.StepResult();

        lifecycle.startStep(testCaseUUID, stepUUId, allureStepResult);

        Action action = step.getAction();
        RequestSpecification httpRequest = buildRequest(action);

        Response response = httpRequest
                .filter(new AllureRestAssured())
                .when()
                .get(buildLink(host, port))
                .then()
                .extract()
                .response();

        boolean testResult = validateResponse(response, step.getExpectedResponse(), allureStepResult);
        lifecycle.stopStep();

        return testResult;
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

    private boolean validateResponse(Response response, ExpectedResponse expectedResponse,
                                  StepResult allureStepResult) {
        boolean isValid = true;
        String failMessage = "";
        if (expectedResponse.getBody() != null) {
            isValid = expectedResponse.getBody().equals(response.getBody().asString());
            if (!isValid) {
                failMessage = new StringBuilder().append(bodyMissmatchMessage.getMessage())
                        .toString();
            }
        }

        if (expectedResponse.getHeaders() != null && isValid) {
            failMessage = expectedResponse
                    .getHeaders().entrySet().stream()
                    .filter(entry -> response.getHeader(entry.getKey()) == null || !entry.getValue().equals(response.getHeader(entry.getKey())))
                    .findFirst()
                    .map(entry -> {
                        if (response.getHeader(entry.getKey()) == null) {
                            return new StringBuilder().append(cantFindHeaderMessage.getMessage())
                                    .append(entry.getKey())
                                    .append("\"").toString();
                        }
                        return new StringBuilder().append(unexpectedHeaderMessage.getMessage())
                                .append(entry.getKey())
                                .append(expecting.getMessage())
                                .append(entry.getValue())
                                .append(butGet.getMessage())
                                .append(response.getHeader(entry.getKey()))
                                .append(point.getMessage()).toString();
                    }).orElse("");

        }

        if (expectedResponse.getHttpCode() != null && isValid) {
            isValid = expectedResponse.getHttpCode().equals(response.statusCode());
            if (!isValid) {
                failMessage = new StringBuilder().append(wrongResponseCodeMessage.getMessage())
                        .append(expectedResponse.getHttpCode())
                        .append(butGet.getMessage())
                        .append(response.getStatusCode()).toString();
            }
        }

        allureStepResult.setStatus(isValid ? Status.PASSED : Status.FAILED);
        StatusDetails statusDetails = new StatusDetails();

        if (!isValid) {
            statusDetails.setMessage(failMessage);
            allureStepResult.setStatusDetails(statusDetails);
        }

        return isValid;
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
