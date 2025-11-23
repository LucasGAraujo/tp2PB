package org.example.dao;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.example.model.Produto;
import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(Produto.class)
public interface ProdutoDAO {

    @SqlUpdate("INSERT INTO produto (nome, preco, estoque) VALUES (:nome, :preco, :estoque)")
    @GetGeneratedKeys
    int criar(@BindBean Produto produto);

    @SqlQuery("SELECT * FROM produto ORDER BY nome")
    List<Produto> listarTodos();

    @SqlQuery("SELECT * FROM produto WHERE id = :id")
    Optional<Produto> buscarPorId(@Bind("id") int id);

    @SqlUpdate("UPDATE produto SET nome = :nome, preco = :preco, estoque = :estoque WHERE id = :id")
    void atualizar(@BindBean Produto produto);

    @SqlUpdate("DELETE FROM produto WHERE id = :id")
    void deletar(@Bind("id") int id);
}