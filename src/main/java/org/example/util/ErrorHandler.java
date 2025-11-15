package org.example.util;

import io.javalin.http.Context;

public class ErrorHandler {

    public static void handleError(Context ctx, String userMessage, Exception e) {
        // Log interno do erro (não expõe detalhes para o usuário)
        System.err.println("Erro: " + e.getMessage());
        e.printStackTrace();

        // Envia mensagem amigável para o usuário
        ctx.status(500).result(userMessage);
    }

    public static void handleNotFound(Context ctx, String resource) {
        ctx.status(404).result(resource + " não encontrado.");
    }
}

