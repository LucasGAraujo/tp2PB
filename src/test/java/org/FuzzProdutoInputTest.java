package org;

import org.example.model.Produto;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FuzzProdutoInputTest {

    @Test
    void fuzzTestEntradasAleatorias() {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            String nome = gerarNomeAleatorio();
            double preco = r.nextDouble() * 10000 - 5000;
            int estoque = r.nextInt(200) - 100;

            assertDoesNotThrow(() -> {
                Produto p = new Produto(nome, Math.abs(preco), Math.max(estoque, 0));
            });
        }
    }

    private String gerarNomeAleatorio() {
        String[] inputs = {"<script>", "DROP TABLE produto;", "Produto", "''", "\"", "çãõ"};
        return inputs[new Random().nextInt(inputs.length)];
    }
}
