package com.example.demorestassured.models.allurePojoes;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AllureContainer {
    private String uuid;
    private String name;
    private ArrayList<String> childrens;
    private ArrayList<Object> befores;
    private ArrayList<Object> afters;
    private long start;
    private long stop;

    public void addChildren(String children) {
        if (childrens == null) {
            childrens = new ArrayList<>();
        }
        childrens.add(children);
    }
}
