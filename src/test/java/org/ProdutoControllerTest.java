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
    private ProdutoController produtoController;

    @BeforeEach
    void setUp() {
        produtoDAO = mock(ProdutoDAO.class);
        ctx = mock(Context.class);

        produtoController = new ProdutoController(produtoDAO);
    }


    @Test
    void deveListarProdutos() {
        when(produtoDAO.listarTodos()).thenReturn(Arrays.asList(
                new Produto(1, "Produto 1", 10.0, 5),
                new Produto(2, "Produto 2", 20.0, 3)
        ));

        produtoController.listarProdutos(ctx);

        verify(produtoDAO).listarTodos();
        verify(ctx).render(eq("layout.html"), anyMap());
    }


    @Test
    void deveExibirFormularioEdicaoProdutoExistente() {
        int produtoId = 1;
        Produto produtoExistente = new Produto(produtoId, "Produto 1", 10.0, 5);

        when(produtoDAO.buscarPorId(produtoId)).thenReturn(Optional.of(produtoExistente));

        when(ctx.pathParam("id")).thenReturn(String.valueOf(produtoId));

        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(mock(io.javalin.validation.Validator.class));
        when(ctx.pathParamAsClass("id", Integer.class).get()).thenReturn(produtoId);

        produtoController.exibirFormularioEdicao(ctx);

        verify(produtoDAO).buscarPorId(produtoId);
        verify(ctx).render(eq("layout.html"), anyMap());
    }


    @Test
    void deveSalvarNovoProduto() {
        when(ctx.formParam("id")).thenReturn(null);
        when(ctx.formParam("nome")).thenReturn("Novo Produto");
        when(ctx.formParam("preco")).thenReturn("15.0");
        when(ctx.formParam("estoque")).thenReturn("2");

        when(ctx.formParamAsClass("preco", Double.class)).thenReturn(mock(io.javalin.validation.Validator.class));
        when(ctx.formParamAsClass("preco", Double.class).get()).thenReturn(15.0);
        when(ctx.formParamAsClass("estoque", Integer.class)).thenReturn(mock(io.javalin.validation.Validator.class));
        when(ctx.formParamAsClass("estoque", Integer.class).get()).thenReturn(2);


        produtoController.salvarProduto(ctx);
        ArgumentCaptor<Produto> produtoCaptor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoDAO).criar(produtoCaptor.capture());

        Produto produtoSalvo = produtoCaptor.getValue();
        assertEquals("Novo Produto", produtoSalvo.getNome());
        assertEquals(0, produtoSalvo.getId());

        verify(ctx).redirect("/produtos");
    }

    @Test
    void deveDeletarProduto() {
        int produtoId = 1;
        when(ctx.pathParam("id")).thenReturn(String.valueOf(produtoId));

        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(mock(io.javalin.validation.Validator.class));
        when(ctx.pathParamAsClass("id", Integer.class).get()).thenReturn(produtoId);

        produtoController.deletarProduto(ctx);

        verify(produtoDAO).deletar(produtoId);
        verify(ctx).redirect("/produtos");
    }
}