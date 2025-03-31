package com.example.demorestassured.alluregen;

import com.example.demorestassured.models.allurePojoes.*;
import com.example.demorestassured.models.response.testModels.ChecklistResult;
import com.example.demorestassured.models.response.testModels.TestCaseResult;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AllureRepGenerator {

    private static final String filepath = "C:\\fineWork\\at_test\\demoRestAssured\\target\\allure-results";

    public static void generateAllureResults(ChecklistResult checklistResults) {
        AllureContainer mainContainer = new AllureContainer();
        List<TestCaseResult> testCaseResults = checklistResults.getResults();
        mainContainer.setUuid(UUID.randomUUID().toString());
        mainContainer.setStart(checklistResults.getStart());
        mainContainer.setStop(checklistResults.getStop());
        mainContainer.setName(checklistResults.getName());

        Map<AllureContainer, AllureResult> containers = new HashMap<>();
        testCaseResults.forEach(tcr -> {
            containers.putAll(generateAllureContainer(tcr));
        });

        containers.forEach((cont, res) -> {
            mainContainer.addChildren(res.getUuid());
        });

        try {
            generateFiles(mainContainer, containers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<AllureContainer, AllureResult> generateAllureContainer(TestCaseResult tcr) {
        AllureContainer ac = new AllureContainer();
        ac.setStart(tcr.getStart());
        ac.setStop(tcr.getStop());
        ac.setName(tcr.getTestCaseId().toString());
        ac.setUuid(UUID.randomUUID().toString());

        AllureResult ar = generateAllureResult(tcr);

        ac.addChildren(ar.getUuid());
        return Collections.singletonMap(ac, ar);
    }

    private static AllureResult generateAllureResult (TestCaseResult tcr) {
        AllureResult ar = new AllureResult();
        ar.setStart(tcr.getStart());
        ar.setStop(tcr.getStop());
        ar.setTestCaseName(tcr.getTestCaseId().toString());
        ar.setTestCaseId(tcr.getTestCaseId().toString());
        ar.setFullName(tcr.getTestCaseId().toString());
        ar.setUuid(UUID.randomUUID().toString());
        ar.setHistoryId(Integer.toString(ThreadLocalRandom.current().nextInt(1, 111)));
        ar.setLabels(buildLabels(tcr));
        ar.setName(tcr.getTestCaseId().toString());
        ar.setStatus(tcr.getTestCaseResult() ? "passed" : "failed");

        ar.setStage("finished");
        ar.setDescription("");
        ar.setSteps(buildSteps(tcr));
        return ar;
    }

    private static ArrayList<AllureLabel> buildLabels(TestCaseResult tcr) {
        ArrayList<AllureLabel> labels = new ArrayList<>();
        labels.add(new AllureLabel("host", getHostName()));
        labels.add(new AllureLabel("framework", "hermes AT"));
        labels.add(new AllureLabel("language", "java"));
        labels.add(new AllureLabel("testMethod", tcr.getTestCaseId().toString()));
        labels.add(new AllureLabel("testClass", tcr.getTestCaseId().toString()));
        labels.add(new AllureLabel("suite", tcr.getTestCaseId().toString()));
        return labels;
    }

    private static ArrayList<AllureStep> buildSteps(TestCaseResult tcr) {
        ArrayList<AllureStep> steps = new ArrayList<>();
        tcr.getStepResults()
                .forEach(step -> {
                    AllureStep aStep = new AllureStep();
                    aStep.setName(step.getStepId().toString());
                    aStep.setStart(step.getStart());
                    aStep.setStop(step.getStop());
                    aStep.setStage("finished");
                    aStep.setStatus(step.getStepResult() ? "passed" : "failed");
                    if (!step.getStepResult()) {
                        AllureStatusDetails asd = new AllureStatusDetails();
                        asd.setMessage(step.getFailMessage());
                        asd.setTrace("");
                        asd.setKnown(false);
                        asd.setFlaky(false);
                        asd.setMuted(false);
                        aStep.setStatusDetails(asd);
                    }
                    steps.add(aStep);
                });
        return steps;
    }

    private static String getHostName() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME");
    }

    private static void generateFiles(AllureContainer container, Map<AllureContainer, AllureResult> resultingContainers) throws IOException {
        Gson gson = new Gson();
        try (FileWriter fw = new FileWriter(filepath + container.getUuid() + "-container.json")) {
            gson.toJson(container, fw);
        }

        resultingContainers.forEach((cont, res) ->
        {
            try (FileWriter fw = new FileWriter(filepath + cont.getUuid() + "-container.json")) {
                gson.toJson(cont, fw);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (FileWriter fw = new FileWriter(filepath + res.getUuid() + "-result.json")) {
                gson.toJson(res, fw);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
