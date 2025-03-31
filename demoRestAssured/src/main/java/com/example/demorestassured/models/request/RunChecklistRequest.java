package com.example.demorestassured.models.request;

import lombok.Data;

@Data
public class RunChecklistRequest {
    private Long checklistId;
    private String host;
    private String port;
}
