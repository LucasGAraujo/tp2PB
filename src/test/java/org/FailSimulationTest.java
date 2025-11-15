package org;

import org.example.model.Produto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FailSimulationTest {

    @Test
    void deveFalharGraciosamenteComEntradaInvalida() {
        Produto p = new Produto(null, -10.0, -5);
        assertThrows(IllegalArgumentException.class, () -> {
            validarProduto(p);
        });
    }

    private void validarProduto(Produto p) {
        if (p.getNome() == null || p.getNome().isEmpty())
            throw new IllegalArgumentException("Nome inválido");
        if (p.getPreco() <= 0)
            throw new IllegalArgumentException("Preço inválido");
        if (p.getEstoque() < 0)
            throw new IllegalArgumentException("Estoque inválido");
    }
}
