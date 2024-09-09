package com.bengous.e2e.tests.jsonplaceholder;

import com.bengous.e2e.common.TestType;
import com.bengous.e2e.common.assertions.JsonAssertion;
import com.bengous.e2e.common.client.RestAssuredClient;
import com.bengous.e2e.common.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.testng.Tag;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.intellij.lang.annotations.Language;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
public class BSimpleTests extends BaseTest {
    @Test(description = "Get the first post", groups = {TestType.SIMPLE})
    @Description("Get the first post from jsonplaceholder API")
    @Tag("jsonplaceholder")
    @Tag("scenario")
    public void test1() {
        Response response = RestAssuredClient.get("/posts/1");

        String actual = response.getBody().asString();
        @Language("json") final String expected = """
                {
                  "userId": 1,
                  "id": 1,
                  "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                  "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto"
                }
                """;

        JsonAssertion.areEquals("Should return the expected 1 post", expected, actual);
    }

    @Test(description = "Get all posts", groups = {TestType.SIMPLE})
    @Description("Get all posts from jsonplaceholder API")
    @Tag("jsonplaceholder")
    @Tag("scenario")
    public void test2() throws IOException {
        Response response = RestAssuredClient.get("/posts");

        String actual = response.getBody().asString();
        String expected = Files.readString(Path.of("src/test/resources/jsonplaceholder/template/compare/get_all_posts.json"));

        JsonAssertion.areEquals("Should return the all the posts", expected, actual);
    }
}