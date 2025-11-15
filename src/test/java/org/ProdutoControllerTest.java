package org;

import io.javalin.http.Context;
import org.example.Controller.ProdutoController;
import org.example.dao.ProdutoDAO;
import org.example.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoControllerTest {

    private ProdutoDAO produtoDAO;
    private Context ctx;

    @BeforeEach
    void setUp() {
        produtoDAO = mock(ProdutoDAO.class);
        ctx = mock(Context.class);

        new ProdutoController(produtoDAO);
    }

    @Test
    void deveListarProdutos() {
        when(produtoDAO.listarTodos()).thenReturn(Arrays.asList(
                new Produto("Produto 1", 10.0, 5),
                new Produto("Produto 2", 20.0, 3)
        ));

        ProdutoController.listarProdutos(ctx);

        verify(ctx).render(eq("layout.html"), anyMap());
    }

    @Test
    void deveExibirFormularioEdicaoProdutoExistente() {
        Produto p = new Produto("Produto 1", 10.0, 5);
        p.setId(1);
        when(produtoDAO.buscarPorId(1)).thenReturn(Optional.of(p));
        when(ctx.pathParam("id")).thenReturn("1");

        ProdutoController.exibirFormularioEdicao(ctx);

        verify(ctx).render(eq("layout.html"), anyMap());
    }


    @Test
    void deveSalvarNovoProduto() {
        when(ctx.formParam("id")).thenReturn(null);
        when(ctx.formParam("nome")).thenReturn("Novo Produto");
        when(ctx.formParam("preco")).thenReturn("15.0");
        when(ctx.formParam("estoque")).thenReturn("2");

        ProdutoController.salvarProduto(ctx);

        verify(produtoDAO).criar(any(Produto.class));
        verify(ctx).redirect("/produtos");
    }

    @Test
    void deveDeletarProduto() {
        when(ctx.pathParam("id")).thenReturn("1");

        ProdutoController.deletarProduto(ctx);

        verify(produtoDAO).deletar(1);
        verify(ctx).redirect("/produtos");
    }
}
