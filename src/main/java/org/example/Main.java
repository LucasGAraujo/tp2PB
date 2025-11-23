package org.example;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.example.Controller.UserController;
import org.example.exception.ValidationException;
import org.example.model.User;

import org.example.Controller.ProdutoController;
import org.example.dao.ProdutoDAO;

import org.h2.jdbcx.JdbcDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.NoSuchElementException;

public class Main {

    public static void main(String[] args) throws Exception {
        startApp(7929);
    }

    public static Javalin startApp(int port) {
        try {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:crud_db;DB_CLOSE_DELAY=-1");

            Jdbi jdbi = Jdbi.create(dataSource);
            jdbi.installPlugin(new SqlObjectPlugin());

            try (InputStream is = Main.class.getResourceAsStream("/db/init.sql")) {
                if (is == null) {
                    throw new RuntimeException("ERRO CRÍTICO: Arquivo /db/init.sql não encontrado dentro do JAR/Classpath.");
                }
                String initSql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                jdbi.useHandle(handle -> handle.execute(initSql));
            }
            ProdutoDAO produtoDAO = jdbi.onDemand(ProdutoDAO.class);


            ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
            resolver.setPrefix("/templates/");
            resolver.setSuffix(".html");
            resolver.setCharacterEncoding("UTF-8");

            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(resolver);


            Javalin app = Javalin.create(config -> {
                config.fileRenderer(new JavalinThymeleaf(templateEngine));
                config.staticFiles.add("/public");
            }).start(port);
            app.before(ctx -> {
                ctx.header("X-Frame-Options", "DENY");
                ctx.header("X-Content-Type-Options", "nosniff");
                ctx.header("Content-Security-Policy", "default-src 'self'; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline'; img-src 'self' data:;");
                ctx.header("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
                ctx.header("Referrer-Policy", "no-referrer-when-downgrade");
            });
            // =================================================================================


            UserController userController = new UserController();
            ProdutoController produtoController = new ProdutoController(produtoDAO);

            app.get("/", ctx -> ctx.render("index.html"));

            app.get("/users", userController::list);
            app.get("/users/new", userController::createForm);
            app.post("/users", userController::create);
            app.get("/users/edit/{id}", userController::editForm);
            app.post("/users/update/{id}", userController::update);
            app.get("/users/delete/{id}", userController::delete);

            app.get("/produtos", produtoController::listarProdutos);
            app.get("/produtos/novo", produtoController::exibirFormularioCadastro);
            app.get("/produtos/editar/{id}", produtoController::exibirFormularioEdicao);
            app.post("/produtos/salvar", produtoController::salvarProduto);
            app.post("/produtos/deletar/{id}", produtoController::deletarProduto);

            app.exception(ValidationException.class, (e, ctx) -> {
                User submittedUser = new User(ctx.formParam("name"), ctx.formParam("email"));

                if (ctx.pathParamMap().containsKey("id")) {
                    try {

                    } catch (NumberFormatException ignored) {}
                }

                ctx.status(400);
                ctx.render("user/user-form.html", Map.of(
                        "error", e.getMessage(),
                        "user", submittedUser
                ));
            });

            app.exception(NoSuchElementException.class, (e, ctx) -> {
                ctx.status(404).result("Recurso não encontrado: " + e.getMessage());
            });

            return app;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao iniciar o sistema integrado", e);
        }
    }
}