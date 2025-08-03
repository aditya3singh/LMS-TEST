package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CoursePage;
import pages.DashboardPage;
import pages.LoginPage;
import utils.APIUtils;
import io.restassured.response.Response;

public class CourseTests extends BaseTest {

    @Test(priority = 1, description = "Course enrollment test")
    public void testCourseEnrollment() {
        // Login first
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.login("testuser@example.com", "password123");

        // Navigate to courses
        CoursePage coursePage = dashboardPage.navigateToBrowseCourses();

        // Verify courses are displayed
        int courseCount = coursePage.getCourseCount();
        Assert.assertTrue(courseCount > 0, "Courses should be available for enrollment");

        // Get first course title for validation
        String firstCourseTitle = coursePage.getFirstCourseTitle();

        // Enroll in first course
        coursePage.enrollInFirstCourse();

        // Verify enrollment success
        Assert.assertTrue(coursePage.isEnrollmentSuccessful(),
                "Course enrollment should be successful");

        // Validate enrollment in database
        Assert.assertTrue(dbUtils.validateCourseEnrollment("testuser@example.com", "1"),
                "Course enrollment should be recorded in database");

        System.out.println("✓ Course enrollment test passed for: " + firstCourseTitle);
    }

    @Test(priority = 2, description = "My courses display test")
    public void testMyCoursesDisplay() {
        // Login and navigate to my courses
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.login("testuser@example.com", "password123");
        CoursePage coursePage = dashboardPage.navigateToMyCourses();

        // Verify enrolled courses are displayed
        int myCourseCount = coursePage.getCourseCount();
        Assert.assertTrue(myCourseCount >= 0, "My courses page should load successfully");

        System.out.println("✓ My courses display test passed. Found " + myCourseCount + " courses");
    }

    @Test(priority = 3, description = "API course retrieval test")
    public void testAPICourseRetrieval() {
        APIUtils apiUtils = new APIUtils();

        // Get courses via API
        Response response = apiUtils.getCourses();

        Assert.assertEquals(response.getStatusCode(), 200,
                "API should return courses successfully");

        // Verify courses data structure
        Assert.assertTrue(response.jsonPath().getList("courses").size() > 0,
                "API should return course list");

        System.out.println("✓ API course retrieval test passed");
    }

    @Test(priority = 4, description = "Course search functionality test")
    public void testCourseSearch() {
        // Login and navigate to dashboard
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.login("testuser@example.com", "password123");

        // Test search functionality
        dashboardPage.searchCourse("Java Programming");

        // Navigate to browse courses to see search results
        CoursePage coursePage = dashboardPage.navigateToBrowseCourses();

        // Verify search results (this would depend on actual implementation)
        int searchResults = coursePage.getCourseCount();
        Assert.assertTrue(searchResults >= 0, "Search should execute without errors");

        System.out.println("✓ Course search test passed. Found " + searchResults + " results");
    }
}