package com.example.demorestassured;


import io.qameta.allure.Allure;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;


@SpringBootTest
class DemoRestAssuredApplicationTests {

    @Test
    void contextLoads() {
        Allure.step("Build url", () -> {
            String url = "https://httpbin.org/status/{code}";
        });


        Map<String, String> params = new HashMap<>();
        params.put("code", "200");

       /*RequestSpecification rq = new RequestSpecBuilder()
                        .addFilter(new AllureRestAssured())
                                .build();
                        */
        Allure.step("Assert http code", () -> {
            given()
                    .filter(new AllureRestAssured())
                    .pathParams(params)
                    .get("https://httpbin.org/status/{code}")
                    .then()
                    .assertThat()
                    .statusCode(200);
        });

    }

    @Test
    void testHttpFail() {
        Allure.step("Build url", () -> {
            String url = "https://httpbin.org/status/{code}";
        });


        Map<String, String> params = new HashMap<>();
        params.put("code", "200");

       /*RequestSpecification rq = new RequestSpecBuilder()
                        .addFilter(new AllureRestAssured())
                                .build();
                        */
        Allure.step("Assert http code", () -> {
            given()
                    .filter(new AllureRestAssured())
                    .pathParams(params)
                    .get("https://httpbin.org/status/{code}")
                    .then()
                    .assertThat()
                    .statusCode(401);
        });
    }

}
