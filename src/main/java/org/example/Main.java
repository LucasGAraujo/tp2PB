package org.example;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.example.Controller.UserController;
import org.example.exception.ValidationException;
import org.example.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
            resolver.setPrefix("/templates/");
            resolver.setSuffix(".html");
            resolver.setCharacterEncoding("UTF-8");
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(resolver);
            config.fileRenderer(new JavalinThymeleaf(templateEngine));
            config.staticFiles.add("/public");
        }).start(7929);

        UserController userController = new UserController();

        app.get("/", ctx -> ctx.render("index.html"));
        app.get("/users", userController::list);
        app.get("/users/new", userController::createForm);
        app.post("/users", userController::create);
        app.get("/users/edit/{id}", userController::editForm);
        app.post("/users/update/{id}", userController::update);
        app.get("/users/delete/{id}", userController::delete);

        app.exception(ValidationException.class, (e, ctx) -> {
            User submittedUser = new User(ctx.formParam("name"), ctx.formParam("email"));
            if (ctx.pathParamMap().containsKey("id")) {
                try {
                    long id = Long.parseLong(ctx.pathParam("id"));
                    submittedUser.setId(id);
                } catch (NumberFormatException ignored) {
                }
            }

            ctx.status(400);
            ctx.render("user-form.html", Map.of(
                    "error", e.getMessage(),
                    "user", submittedUser
            ));
        });

        app.exception(NoSuchElementException.class, (e, ctx) -> {
            ctx.status(404).result("Recurso não encontrado: " + e.getMessage());
        });
    }
}