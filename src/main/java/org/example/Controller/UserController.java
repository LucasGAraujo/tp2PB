package org.example.Controller;

import io.javalin.http.Context;
import org.example.exception.ValidationException;
import org.example.model.User;
import org.example.repository.UserRepository;

import java.util.HashMap;

public class UserController {

    private final UserRepository repo = new UserRepository();

    public void list(Context ctx) {
        var model = new HashMap<String, Object>();
        model.put("users", repo.findAll());
        ctx.render("users.html", model);
    }

    public void createForm(Context ctx) {
        var model = new HashMap<String, Object>();
        model.put("user", User.vazio());
        ctx.render("user-form.html", model);
    }

    public void create(Context ctx) {
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");

        validarCampos(name, email);

        if (repo.findByEmail(email).isPresent()) {
            throw new ValidationException("Este e-mail já está cadastrado.");
        }

        User novoUser = new User(name, email);
        repo.save(novoUser);

        ctx.redirect("/users");
    }

    public void editForm(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        User user = repo.findById(id);

        if (user == null) {
            throw new ValidationException("Usuário não encontrado.");
        }

        var model = new HashMap<String, Object>();
        model.put("user", user);
        ctx.render("user-form.html", model);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");

        validarCampos(name, email);

        User existingUser = repo.findById(id);
        if (existingUser == null) {
            throw new ValidationException("Usuário não encontrado.");
        }

        User updatedUser = existingUser.atualizar(name, email);

        repo.update(updatedUser);

        ctx.redirect("/users");
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        repo.delete(id);
        ctx.redirect("/users");
    }

    private void validarCampos(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("O campo 'Nome' é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new ValidationException("O campo 'Email' é obrigatório.");
        }
    }
}
