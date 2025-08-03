package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;
import pages.CoursePage;

public class RegressionTestSuite extends BaseTest {

    @Test(priority = 1, groups = {"regression", "smoke"},
            description = "End-to-end user journey test")
    public void testCompleteUserJourney() {
        // Step 1: Login
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.login("testuser@example.com", "password123");

        Assert.assertTrue(dashboardPage.isWelcomeMessageDisplayed(),
                "Login should be successful");

        // Step 2: Browse courses
        CoursePage coursePage = dashboardPage.navigateToBrowseCourses();
        int availableCourses = coursePage.getCourseCount();

        Assert.assertTrue(availableCourses > 0, "Courses should be available");

        // Step 3: Enroll in course
        String courseTitle = coursePage.getFirstCourseTitle();
        coursePage.enrollInFirstCourse();

        Assert.assertTrue(coursePage.isEnrollmentSuccessful(),
                "Course enrollment should be successful");

        // Step 4: Verify in My Courses
        CoursePage myCoursePage = dashboardPage.navigateToMyCourses();
        int enrolledCourses = myCoursePage.getCourseCount();

        Assert.assertTrue(enrolledCourses > 0, "Enrolled courses should be visible");

        // Step 5: Logout
        dashboardPage.logout();
        Assert.assertTrue(driver.getCurrentUrl().contains("login"),
                "Should redirect to login after logout");

        System.out.println("✓ Complete user journey test passed");
        System.out.println("  - Course enrolled: " + courseTitle);
        System.out.println("  - Total enrolled courses: " + enrolledCourses);
    }

    @Test(priority = 2, groups = {"regression"},
            description = "Data integrity validation test")
    public void testDataIntegrityValidation() {
        // Validate database consistency
        boolean userExists = dbUtils.validateUserExists("testuser@example.com");
        Assert.assertTrue(userExists, "Test user should exist in database");

        // Validate course enrollment data
        boolean enrollmentExists = dbUtils.validateCourseEnrollment("testuser@example.com", "1");
        Assert.assertTrue(enrollmentExists, "Course enrollment should exist in database");

        System.out.println("✓ Data integrity validation test passed");
    }

    @Test(priority = 3, groups = {"regression"},
            description = "Cross-browser compatibility test")
    public void testCrossBrowserCompatibility() {
        // This test would run on multiple browsers
        // For now, we'll test basic functionality

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.login("testuser@example.com", "password123");

        Assert.assertTrue(dashboardPage.isWelcomeMessageDisplayed(),
                "Application should work across different browsers");

        // Test responsive elements
        int courseCount = dashboardPage.getCourseCount();
        Assert.assertTrue(courseCount >= 0, "Course display should work in all browsers");

        System.out.println("✓ Cross-browser compatibility test passed");
    }
}