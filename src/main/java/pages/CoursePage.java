package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

public class CoursePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(className = "course-item")
    private List<WebElement> courseItems;

    @FindBy(className = "enroll-btn")
    private List<WebElement> enrollButtons;

    @FindBy(className = "course-title")
    private List<WebElement> courseTitles;

    @FindBy(id = "filterByCategory")
    private WebElement categoryFilter;

    @FindBy(className = "success-message")
    private WebElement successMessage;

    public CoursePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    public int getCourseCount() {
        wait.until(ExpectedConditions.visibilityOfAllElements(courseItems));
        return courseItems.size();
    }

    public void enrollInFirstCourse() {
        wait.until(ExpectedConditions.elementToBeClickable(enrollButtons.get(0)));
        enrollButtons.get(0).click();
    }

    public void enrollInCourse(String courseName) {
        for (int i = 0; i < courseTitles.size(); i++) {
            if (courseTitles.get(i).getText().contains(courseName)) {
                enrollButtons.get(i).click();
                break;
            }
        }
    }

    public boolean isEnrollmentSuccessful() {
        try {
            wait.until(ExpectedConditions.visibilityOf(successMessage));
            return successMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getFirstCourseTitle() {
        wait.until(ExpectedConditions.visibilityOfAllElements(courseTitles));
        return courseTitles.get(0).getText();
    }
}