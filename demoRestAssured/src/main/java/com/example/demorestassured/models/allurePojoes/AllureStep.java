package com.example.demorestassured.models.allurePojoes;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AllureStep {
        private String name;
        private String status;
        private String stage;
        private ArrayList<AllureStep> steps;
        private ArrayList<Object> attachments;
        private ArrayList<Object> parameters;
        private long start;
        private long stop;
        private AllureStatusDetails statusDetails;
}
