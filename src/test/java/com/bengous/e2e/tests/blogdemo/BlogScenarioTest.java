package com.bengous.e2e.tests.blogdemo;

import com.bengous.e2e.common.BaseTest;
import com.bengous.e2e.common.TestType;
import com.bengous.e2e.common.allure.StepChain;
import com.bengous.e2e.common.assertions.JsonAssertion;
import com.bengous.e2e.common.client.MimeType;
import com.bengous.e2e.common.client.PartBuilder;
import com.bengous.e2e.common.client.RestAssuredClient;
import com.bengous.e2e.common.file.FileUtils;
import com.bengous.e2e.tests.blogdemo.context.BlogDemoContext;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.testng.Tag;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.intellij.lang.annotations.Language;
import org.testng.annotations.Test;

import java.util.Map;

@Log4j2
public class BlogScenarioTest extends BaseTest {

    @Test(
        description = "Cycle de vie complet d'un article de blog",
        groups = {TestType.SCENARIO}
    )
    @Description("Scénario E2E : création utilisateur → article → upload → mise à jour → suppression")
    @Tag("blog-demo")
    @Tag("scenario")
    public void blogLifecycle() {
        var initialContext = new BlogDemoContext(0, 0, null);

        new StepChain<>(initialContext)

            // ── Step 1 : Créer un utilisateur ──────────────────────────────
            .runStep("Créer un utilisateur", context -> {
                @Language("json") final String body = """
                    {
                      "name": "Demo User",
                      "email": "demo@e2e-test.com",
                      "role": "editor"
                    }
                    """;

                Response response = RestAssuredClient.post("/users",
                    RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(body));

                Allure.step("Le status code doit être 201", () ->
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(201));

                int userId = response.jsonPath().getInt("id");
                Allure.step("L'ID utilisateur doit être > 0", () ->
                    Assertions.assertThat(userId).isGreaterThan(0));

                @Language("json") final String expected = """
                    {
                      "name": "Demo User",
                      "email": "demo@e2e-test.com",
                      "role": "editor"
                    }
                    """;
                JsonAssertion.areEquals("L'utilisateur retourné correspond", expected, response.getBody().asString());

                return context.toBuilder().userId(userId).build();
            })

            // ── Step 2 : Créer un article pour cet utilisateur ─────────────
            .runStep("Créer un article avec template dynamique", context -> {
                String postBody = FileUtils.readResourceAsString(
                    "blogdemo/template/request/create_post.json");
                postBody = postBody
                    .replace("{{userId}}", String.valueOf(context.userId()));

                Response response = RestAssuredClient.post("/posts",
                    RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(postBody));

                Allure.step("Le status code doit être 201", () ->
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(201));

                int postId = response.jsonPath().getInt("id");
                Allure.step("L'ID de l'article doit être > 0", () ->
                    Assertions.assertThat(postId).isGreaterThan(0));

                return context.toBuilder().postId(postId).build();
            })

            // ── Step 3 : Lire et valider l'article créé ────────────────────
            .runStep("Lire et valider l'article créé", context -> {
                Response response = RestAssuredClient.get("/posts/" + context.postId());

                Allure.step("Le status code doit être 200", () ->
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(200));

                String actual = response.getBody().asString();
                String expected = FileUtils.readResourceAsString(
                    "blogdemo/template/compare/expected_post.json")
                    .replace("{{userId}}", String.valueOf(context.userId()))
                    .replace("{{postId}}", String.valueOf(context.postId()));

                JsonAssertion.areEquals("L'article correspond au template attendu", expected, actual);

                Allure.step("Le titre doit correspondre", () ->
                    Assertions.assertThat(response.jsonPath().getString("title"))
                        .isEqualTo("Mon article de démonstration E2E"));

                return context;
            })

            // ── Step 4 : Mettre à jour l'article ───────────────────────────
            .runStep("Mettre à jour l'article", context -> {
                @Language("json") final String updateBody = """
                    {
                      "userId": %d,
                      "title": "Article mis à jour par le test E2E",
                      "body": "Ce contenu a été modifié par le scénario de test automatisé.",
                      "status": "reviewed"
                    }
                    """.formatted(context.userId());

                Response response = RestAssuredClient.put("/posts/" + context.postId(),
                    RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(updateBody));

                Allure.step("Le status code doit être 200", () ->
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(200));

                Allure.step("Le titre doit être mis à jour", () ->
                    Assertions.assertThat(response.jsonPath().getString("title"))
                        .isEqualTo("Article mis à jour par le test E2E"));

                Allure.step("Le status doit être 'reviewed'", () ->
                    Assertions.assertThat(response.jsonPath().getString("status"))
                        .isEqualTo("reviewed"));

                return context;
            })

            // ── Step 5 : Uploader une pièce jointe (multipart) ─────────────
            .runStep("Uploader une pièce jointe PDF", context -> {
                var filePart = PartBuilder.normal(
                    "file",
                    "Contenu simulé d'un document PDF pour la démo E2E.\n"
                        + "Ceci démontre la capacité d'upload multipart du framework.",
                    MimeType.PDF);

                Response response = RestAssuredClient.post("/upload",
                    RestAssured.given().multiPart(filePart));

                Allure.step("Le status code doit être 201", () ->
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(201));

                String filename = response.jsonPath().getString("filename");
                Allure.step("Le nom du fichier doit être présent", () ->
                    Assertions.assertThat(filename).isNotEmpty());

                Allure.step("Le type MIME doit être application/pdf", () ->
                    Assertions.assertThat(response.jsonPath().getString("mimeType"))
                        .isEqualTo("application/pdf"));

                Allure.step("La taille doit être > 0", () ->
                    Assertions.assertThat(response.jsonPath().getInt("size"))
                        .isGreaterThan(0));

                return context.toBuilder().uploadedFileName(filename).build();
            })

            // ── Step 6 : Lister les articles de l'utilisateur ──────────────
            .runStep("Lister les articles de l'utilisateur", context -> {
                Response response = RestAssuredClient.get(
                    "/posts?userId=" + context.userId());

                Allure.step("Le status code doit être 200", () ->
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(200));

                var posts = response.jsonPath().getList("$");
                Allure.step("Il doit y avoir au moins 1 article", () ->
                    Assertions.assertThat(posts).isNotEmpty());

                Allure.step("Tous les articles doivent appartenir à l'utilisateur", () ->
                    Assertions.assertThat(response.jsonPath().getList("userId", Integer.class))
                        .allMatch(id -> id == context.userId()));

                return context;
            })

            // ── Step 7 : Supprimer l'article ───────────────────────────────
            .runStep("Supprimer l'article", context -> {
                Response response = RestAssuredClient.delete(
                    "/posts/" + context.postId());

                Allure.step("Le status code doit être 200", () ->
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(200));

                return context;
            })

            // ── Step 8 : Vérifier que l'article est supprimé ───────────────
            .runVoidStep("Vérifier la suppression de l'article", context -> {
                Response response = RestAssuredClient.get(
                    "/posts/" + context.postId());

                Allure.step("L'article ne doit plus exister (réponse vide)", () ->
                    Assertions.assertThat(response.getBody().asString())
                        .isIn("{}", ""));
            })

            .execute();
    }
}
