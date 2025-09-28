package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserListPage {

    private final WebDriver driver;

    private final By newUserButton = By.linkText("Novo Usuário");
    private final By userTable = By.tagName("table");

    public UserListPage(WebDriver driver) {
        this.driver = driver;
    }

    public UserFormPage clickAddNewUser() {
        driver.findElement(newUserButton).click();
        return new UserFormPage(driver);
    }

    public boolean isUserInTable(String name, String email) {
        String tableText = driver.findElement(userTable).getText();
        return tableText.contains(name) && tableText.contains(email);
    }

    public UserFormPage clickEditUser(String userName) {
        WebElement userRow = driver.findElement(By.xpath("//td[text()='" + userName + "']/.."));
        userRow.findElement(By.linkText("Editar")).click();
        return new UserFormPage(driver);
    }

    public void clickDeleteUser(String userName) {
        WebElement userRow = driver.findElement(By.xpath("//td[text()='" + userName + "']/.."));
        userRow.findElement(By.linkText("Excluir")).click();
    }

    public String getPageSource() {
        return driver.getPageSource();
    }
}