package org.example.model;

import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public final class Produto {

    private final int id;
    private final String nome;
    private final double preco;
    private final int estoque;

    public Produto(String nome, double preco, int estoque) {
        this(0, nome, preco, estoque);
    }

    @JdbiConstructor
    public Produto(@ColumnName("id") int id,
                   @ColumnName("nome") String nome,
                   @ColumnName("preco") double preco,
                   @ColumnName("estoque") int estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }

    public Produto atualizar(String nome, double preco, int estoque) {
        return new Produto(this.id, nome, preco, estoque);
    }

    public static Produto vazio() {
        return new Produto(0, "", 0.0, 0);
    }
}