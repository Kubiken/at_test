package com.example.demorestassured.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TextTemplates {
    bodyMissmatchMessage("Actual body doesn't match expected, see attachments."),
    cantFindHeaderMessage("Cant find expected header \""),
    unexpectedHeaderMessage("Unexpected value for header \""),
    expecting("\": Expecting: \""),
    butGet("\" but get \""),
    point("\"."),
    wrongResponseCodeMessage("Expect response code ");

    private String message;

}
