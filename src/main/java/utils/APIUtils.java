package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class APIUtils {
    private ConfigReader config;
    private String baseUrl;
    private String apiKey;

    public APIUtils() {
        this.config = new ConfigReader();
        this.baseUrl = config.getProperty("api.base.url");
        this.apiKey = config.getProperty("api.key");
        RestAssured.baseURI = baseUrl;
    }

    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json");
    }

    public Response authenticateUser(String username, String password) {
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password
        );

        return getRequestSpec()
                .body(requestBody)
                .post("/auth/login");
    }

    public Response getUserProfile(String userId) {
        return getRequestSpec()
                .get("/users/" + userId);
    }

    public Response getCourses() {
        return getRequestSpec()
                .get("/courses");
    }

    public Response enrollInCourse(String userId, String courseId) {
        String requestBody = String.format(
                "{\"user_id\":\"%s\",\"course_id\":\"%s\"}",
                userId, courseId
        );

        return getRequestSpec()
                .body(requestBody)
                .post("/enrollments");
    }
}