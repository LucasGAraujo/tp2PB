
// Imports do Selenium e Javalin (do SeleniumUITest)
import io.javalin.Javalin;
import org.example.Main;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Imports do PageObjects e WebDriverManager (do TesteTp2)
import Pages.UserFormPage;
import Pages.UserListPage;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TesteTp2 {

    private static Javalin app;
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void startServer() {
        app = Main.startApp(7929);
    }

    @AfterAll
    static void stopServer() {
        if (app != null) {
            app.stop();
        }
    }

    @BeforeEach
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
    @Test
    @DisplayName("Deve criar um novo produto")
    void deveCriarProduto() {
        driver.get("http://localhost:7929/produtos");
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Novo Produto"))).click();

        driver.findElement(By.name("nome")).sendKeys("Cabo HDMI");
        driver.findElement(By.name("preco")).sendKeys("59.99");
        driver.findElement(By.name("estoque")).sendKeys("15");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
        assertTrue(table.getText().contains("Cabo HDMI"));
    }

    @Test
    @DisplayName("Deve editar um produto")
    void deveEditarProduto() {
        driver.get("http://localhost:7929/produtos");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Editar"))).click();
        WebElement nomeInput = driver.findElement(By.name("nome"));
        nomeInput.clear();
        nomeInput.sendKeys("Cabo HDMI Premium");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
        Assertions.assertTrue(table.getText().contains("Cabo HDMI Premium"));
    }

    @Test
    @DisplayName("Deve excluir um produto")
    void deveExcluirProduto() {
        driver.get("http://localhost:7929/produtos");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
        WebElement firstRow = table.findElement(By.cssSelector("tbody tr:first-child"));
        String nomeProduto = firstRow.findElement(By.cssSelector("td:nth-child(2)")).getText();
        WebElement excluirBtn = firstRow.findElement(By.cssSelector("form button[type='submit']"));
        excluirBtn.click();
        wait.until(ExpectedConditions.invisibilityOfElementWithText(By.cssSelector("table tbody tr td:nth-child(2)"), nomeProduto));
        table = driver.findElement(By.tagName("table"));
        Assertions.assertFalse(table.getText().contains(nomeProduto));
    }

    @Test
    @DisplayName("Não deve permitir campos vazios (Produto)")
    void naoDevePermitirCamposVaziosProduto() {
        driver.get("http://localhost:7929/produtos");
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Novo Produto"))).click();

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.getPageSource().contains("Preencha todos os campos")
                || driver.getCurrentUrl().contains("/novo"));
    }


    @Test
    @DisplayName("Deve cadastrar um novo usuário")
    void testCreateUser() {
        driver.get("http://localhost:7929/users");
        UserListPage userListPage = new UserListPage(driver);

        String timestamp = String.valueOf(System.currentTimeMillis());
        String userName = "Novo Usuario " + timestamp;
        String userEmail = "pom" + timestamp + "@teste.com";

        UserFormPage formPage = userListPage.clickAddNewUser();
        userListPage = formPage.fillName(userName)
                .fillEmail(userEmail)
                .clickSaveButton();

        assertTrue(userListPage.isUserInTable(userName, userEmail),
                "O usuário cadastrado deve estar visível na tabela.");
    }

    @Test
    @DisplayName("Deve editar um usuário")
    void testEditUser() {
        driver.get("http://localhost:7929/users");
        UserListPage userListPage = new UserListPage(driver);

        String timestamp = String.valueOf(System.currentTimeMillis());
        String originalName = "Para Editar " + timestamp;
        String originalEmail = "editar" + timestamp + "@teste.com";
        String updatedName = "Usuario Editado Com Sucesso";

        UserFormPage formPage = userListPage.clickAddNewUser();
        userListPage = formPage.fillName(originalName)
                .fillEmail(originalEmail)
                .clickSaveButton();

        formPage = userListPage.clickEditUser(originalName);
        userListPage = formPage.fillName(updatedName)
                .clickSaveButton();

        assertTrue(userListPage.isUserInTable(updatedName, originalEmail),
                "O nome atualizado deve ser exibido na lista.");
        assertFalse(userListPage.isUserInTable(originalName, originalEmail),
                "O nome original não deve mais existir.");
    }

    @Test
    @DisplayName("Deve excluir um usuário")
    void testDeleteUser() {
        driver.get("http://localhost:7929/users");
        UserListPage userListPage = new UserListPage(driver);

        String timestamp = String.valueOf(System.currentTimeMillis());
        String userNameToDelete = "Para Excluir " + timestamp;
        String userEmailToDelete = "excluir" + timestamp + "@teste.com";

        UserFormPage formPage = userListPage.clickAddNewUser();
        userListPage = formPage.fillName(userNameToDelete)
                .fillEmail(userEmailToDelete)
                .clickSaveButton();

        assertTrue(userListPage.isUserInTable(userNameToDelete, userEmailToDelete),
                "Pré-condição falhou: O usuário a ser excluído não foi criado corretamente.");

        userListPage.clickDeleteUser(userNameToDelete);

        assertFalse(userListPage.isUserInTable(userNameToDelete, userEmailToDelete),
                "O usuário excluído não deve mais aparecer na lista.");
    }
}