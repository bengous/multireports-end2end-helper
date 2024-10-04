package com.bengous.e2e.tests.jsonplaceholder;

import com.bengous.e2e.common.TestType;
import com.bengous.e2e.common.allure.StepChain;
import com.bengous.e2e.common.assertions.JsonAssertion;
import com.bengous.e2e.common.client.RestAssuredClient;
import com.bengous.e2e.common.BaseTest;
import com.bengous.e2e.tests.jsonplaceholder.context.JsonPlaceholderContext;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.testng.Tag;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.intellij.lang.annotations.Language;
import org.testng.annotations.Test;

@Log4j2
public class BScenarioTests extends BaseTest {
    @Test(description = "Get the first and then second post", groups = {TestType.SCENARIO})
    @Description("Get the first post from jsonplaceholder API, then the second")
    @Tag("jsonplaceholder")
    @Tag("scenario")
    public void scenario1() {
        var initialContext = new JsonPlaceholderContext();
        var chain = new StepChain<>(initialContext);
        chain.runVoidStep("Get first post", softly -> {
            Response response = RestAssuredClient.get("/posts/1");
            String actual = response.getBody().asString();
            Allure.step("Should return the expected 1 post", () -> {
                @Language("json") final String expected = """
                        {
                          "userId": 1,
                          "id": 1,
                          "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                          "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto"
                        }
                        """;
                JsonAssertion.areEquals("Should return the expected 1 post", expected, actual);
            });
            Allure.step(

                    "User ID should be 1", () -> {
                        final int userId = response.jsonPath().getInt("userId");
                        Assertions.assertThat(userId).isEqualTo(1000); // FAIL on purpose
                    });
            Allure.step("ID should be 1", () -> {
                final int id = response.jsonPath().getInt("id");
                Assertions.assertThat(id).isEqualTo(1);
            });
        });
        chain.runVoidStep("Get first post", softly -> {
            Response response = RestAssuredClient.get("/posts/1");
            String actual = response.getBody().asString();

            Allure.step("Should return the expected 1 post", () -> {
                @Language("json") final String expected = """
                        {
                          "userId": 1,
                          "id": 1,
                          "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                          "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto"
                        }
                        """;
                JsonAssertion.areEquals("Should return the expected 1 post", expected, actual);
            });
            Allure.step("User ID should be 1", () -> {
                final int userId = response.jsonPath().getInt("userId");
                Assertions.assertThat(userId).isEqualTo(1000); // FAIL on purpose
            });
            Allure.step("ID should be 1", () -> {
                final int id = response.jsonPath().getInt("id");
                Assertions.assertThat(id).isEqualTo(1);
            });
        });
        chain.runVoidStep("Get second post", softly -> {
            Response response = RestAssuredClient.get("/posts/2");

            String actual = response.getBody().asString();
            @Language("json") final String expected = """
                    {
                      "userId": 1,
                      "id": 2,
                      "title": "qui est esse",
                      "body": "est rerum tempore vitae\\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\\nqui aperiam non debitis possimus qui neque nisi nulla"
                    }
                    """;

            JsonAssertion.areEquals("Should return the expected 1 post", expected, actual);
        });
        chain.execute();
    }
}