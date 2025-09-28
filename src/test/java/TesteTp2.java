import Pages.UserFormPage;
import Pages.UserListPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TesteTp2 {

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:7929";
    private UserListPage userListPage;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(BASE_URL + "/users");
        userListPage = new UserListPage(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Deve cadastrar um novo usuário")
    void testCreateUser() {
        // Usa dados únicos para evitar conflito com outros testes
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
    @Order(2)
    @DisplayName("Deve editar um usuário")
    void testEditUser() {
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

        assertFalse(userListPage.getPageSource().contains(originalName),
                "O nome original não deve mais existir.");
        assertTrue(userListPage.getPageSource().contains(updatedName),
                "O nome atualizado deve ser exibido na lista.");
    }

    @Test
    @Order(3)
    @DisplayName("Deve excluir um usuário")
    void testDeleteUser() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String userNameToDelete = "Para Excluir " + timestamp;
        String userEmailToDelete = "excluir" + timestamp + "@teste.com";

        UserFormPage formPage = userListPage.clickAddNewUser();
        userListPage = formPage.fillName(userNameToDelete)
                .fillEmail(userEmailToDelete)
                .clickSaveButton();

        assertTrue(userListPage.getPageSource().contains(userNameToDelete),
                "Pré-condição falhou: O usuário a ser excluído não foi criado corretamente.");

        userListPage.clickDeleteUser(userNameToDelete);
        assertFalse(userListPage.getPageSource().contains(userNameToDelete),
                "O usuário excluído não deve mais aparecer na lista.");
    }}