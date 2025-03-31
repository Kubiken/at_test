package com.example.demorestassured.models.allurePojoes;

import lombok.Data;

@Data
public class AllureStatusDetails {
        private boolean known;
        private boolean muted;
        private boolean flaky;
        private String message;
        private String trace;
}
