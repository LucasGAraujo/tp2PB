package org;

import org.example.dao.ProdutoDAO;
import org.example.model.Produto;
import org.h2.jdbcx.JdbcDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProdutoDAOTest {

    private ProdutoDAO produtoDAO;

    @BeforeAll
    void setup() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1");
        Jdbi jdbi = Jdbi.create(ds);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.useHandle(h -> h.execute(
                "CREATE TABLE produto (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(255), preco DECIMAL(10,2), estoque INT)"
        ));
        produtoDAO = jdbi.onDemand(ProdutoDAO.class);
    }

    @Test
    void deveCriarListarEAtualizarProduto() {
        Produto p = new Produto("Mouse", 99.90, 10);
        int id = produtoDAO.criar(p);

        List<Produto> produtos = produtoDAO.listarTodos();
        Assertions.assertEquals(1, produtos.size());

        Produto salvo = produtos.get(0);
        salvo.setPreco(79.90);
        produtoDAO.atualizar(salvo);

        Produto atualizado = produtoDAO.buscarPorId(id).get();
        Assertions.assertEquals(79.90, atualizado.getPreco());
    }

    @Test
    void deveDeletarProduto() {
        Produto p = new Produto("Teclado", 199.0, 5);
        int id = produtoDAO.criar(p);
        produtoDAO.deletar(id);
        Assertions.assertTrue(produtoDAO.listarTodos().isEmpty());
    }
}
