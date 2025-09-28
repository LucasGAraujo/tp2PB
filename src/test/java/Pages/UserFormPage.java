package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UserFormPage {

    private final WebDriver driver;

    private final By nameInput = By.name("name");
    private final By emailInput = By.name("email");
    private final By saveButton = By.tagName("button");

    public UserFormPage(WebDriver driver) {
        this.driver = driver;
    }

    public UserFormPage fillName(String name) {
        driver.findElement(nameInput).clear();
        driver.findElement(nameInput).sendKeys(name);
        return this;
    }
    public UserFormPage fillEmail(String email) {
        driver.findElement(emailInput).clear();
        driver.findElement(emailInput).sendKeys(email);
        return this;
    }
    public UserListPage clickSaveButton() {
        driver.findElement(saveButton).click();

        return new UserListPage(driver);
    }
}
