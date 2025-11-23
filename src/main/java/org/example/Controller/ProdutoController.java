package org.example.Controller;

import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

import org.example.dao.ProdutoDAO;
import org.example.model.Produto;
import org.example.util.ErrorHandler;

public class ProdutoController {

    private final ProdutoDAO produtoDAO;

    public ProdutoController(ProdutoDAO dao) {
        this.produtoDAO = dao;
    }

    public void listarProdutos(Context ctx) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("produtos", produtoDAO.listarTodos());
            model.put("template", "lista-produtos");

            ctx.render("layout.html", model);

        } catch (Exception e) {
            ErrorHandler.handleError(ctx, "Erro ao listar produtos.", e);
        }
    }

    public void exibirFormularioCadastro(Context ctx) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("produto", Produto.vazio());
            model.put("isEditing", false);
            model.put("template", "form-produto");

            ctx.render("layout.html", model);

        } catch (Exception e) {
            ErrorHandler.handleError(ctx, "Erro ao exibir formulário de cadastro.", e);
        }
    }

    public void exibirFormularioEdicao(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Produto produto = produtoDAO.buscarPorId(id).orElse(null);

            if (produto == null) {
                ErrorHandler.handleNotFound(ctx, "Produto");
                return;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("produto", produto);
            model.put("isEditing", true);
            model.put("template", "form-produto");

            ctx.render("layout.html", model);

        } catch (NumberFormatException e) {
            ctx.status(400).result("ID inválido.");
        } catch (Exception e) {
            ErrorHandler.handleError(ctx, "Erro ao exibir formulário de edição.", e);
        }
    }

    public void salvarProduto(Context ctx) {
        try {
            String idParam = ctx.formParam("id");
            String nome = ctx.formParam("nome");
            double preco = Double.parseDouble(ctx.formParam("preco"));
            int estoque = Integer.parseInt(ctx.formParam("estoque"));

            boolean criando = (idParam == null || idParam.isBlank() || idParam.equals("0"));

            if (criando) {
                Produto novoProduto = new Produto(nome, preco, estoque);
                produtoDAO.criar(novoProduto);

            } else {
                int id = Integer.parseInt(idParam);

                Produto existente = produtoDAO.buscarPorId(id).orElse(null);

                if (existente == null) {
                    ErrorHandler.handleNotFound(ctx, "Produto");
                    return;
                }

                Produto atualizado = existente.atualizar(nome, preco, estoque);

                produtoDAO.atualizar(atualizado);
            }

            ctx.redirect("/produtos");

        } catch (NumberFormatException e) {
            ctx.status(400).result("Valores inválidos no formulário.");
        } catch (Exception e) {
            ErrorHandler.handleError(ctx, "Erro ao salvar produto.", e);
        }
    }

    public void deletarProduto(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            produtoDAO.deletar(id);
            ctx.redirect("/produtos");

        } catch (NumberFormatException e) {
            ctx.status(400).result("ID inválido.");
        } catch (Exception e) {
            ErrorHandler.handleError(ctx, "Erro ao deletar produto.", e);
        }
    }
}
