package org.example.Controller;

import io.javalin.http.Context;
import org.example.exception.ValidationException;
import org.example.model.User;
import org.example.repository.UserRepository;
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
        ctx.render("user-form.html");
    }

    public void create(Context ctx) {
        final UserRepository userRepository = new UserRepository();

        System.out.println(">>> 1. MÉTODO CREATE FOI ACIONADO!"); // Log 1

        String name = ctx.formParam("name");
        String email = ctx.formParam("email");

        System.out.println(">>> 2. NOME RECEBIDO: '" + name + "'"); // Log 2

        if (name == null || name.isBlank()) {
            System.out.println(">>> 3. ERRO: NOME ESTÁ VAZIO! LANÇANDO EXCEÇÃO..."); // Log 3
            throw new ValidationException("O campo 'Nome' é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            System.out.println(">>> 3. ERRO: email ESTÁ VAZIO! LANÇANDO EXCEÇÃO..."); // Log 3
            throw new ValidationException("O campo 'email' é obrigatório.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            System.out.println(">>> 3. ERRO: EMAIL DUPLICADO! LANÇANDO EXCEÇÃO...");
            throw new ValidationException("Este e-mail já está cadastrado.");
        }

        System.out.println(">>> 4. VALIDAÇÃO PASSOU! SALVANDO USUÁRIO..."); // Log 4
        User newUser = new User(name, email);
        userRepository.save(newUser);
        ctx.redirect("/users");
    }
    public void editForm(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        var model = new HashMap<String, Object>();
        model.put("user", repo.findById(id));
        ctx.render("user-form.html", model);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");
        if (name == null || name.isBlank()) {
            throw new ValidationException("O campo 'Nome' não pode ser vazio.");
        }
        User newUser = new User(name, email);
        repo.update(newUser);;
        ctx.redirect("/users");
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        repo.delete(id);
        ctx.redirect("/users");
    }
}
