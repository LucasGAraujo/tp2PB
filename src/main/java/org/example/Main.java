package org.example;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

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
        startApp(7929);  // porta única para os 2 sistemas
    }

    public static Javalin startApp(int port) {
        try {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:crud_db;DB_CLOSE_DELAY=-1");

            Jdbi jdbi = Jdbi.create(dataSource);
            jdbi.installPlugin(new SqlObjectPlugin());

            String initSql = new String(Files.readAllBytes(
                    Paths.get("src/main/resources/db/init.sql")
            ));
            jdbi.useHandle(handle -> handle.execute(initSql));

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

            UserController userController = new UserController();
            new ProdutoController(produtoDAO);

            app.get("/", ctx -> ctx.render("index.html"));

            app.get("/users", userController::list);
            app.get("/users/new", userController::createForm);
            app.post("/users", userController::create);
            app.get("/users/edit/{id}", userController::editForm);
            app.post("/users/update/{id}", userController::update);
            app.get("/users/delete/{id}", userController::delete);
            app.get("/produtos", ProdutoController::listarProdutos);
            app.get("/produtos/novo", ProdutoController::exibirFormularioCadastro);
            app.get("/produtos/editar/{id}", ProdutoController::exibirFormularioEdicao);
            app.post("/produtos/salvar", ProdutoController::salvarProduto);
            app.post("/produtos/deletar/{id}", ProdutoController::deletarProduto);

            app.exception(ValidationException.class, (e, ctx) -> {
                User submittedUser = new User(ctx.formParam("name"), ctx.formParam("email"));

                if (ctx.pathParamMap().containsKey("id")) {
                    try {
                        submittedUser.setId(Long.parseLong(ctx.pathParam("id")));
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
