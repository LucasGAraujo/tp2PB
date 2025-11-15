package org;

import org.example.model.Produto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ParametrizedProdutoTest {

    @ParameterizedTest
    @CsvSource({
            "Notebook, 3500.00, 10",
            "Mouse, 89.90, 50",
            "Monitor, 1200.00, 5"
    })
    void deveCriarProdutoComValoresValidos(String nome, double preco, int estoque) {
        Produto p = new Produto(nome, preco, estoque);
        assertEquals(nome, p.getNome());
        assertTrue(p.getPreco() > 0);
        assertTrue(p.getEstoque() >= 0);
    }
}
