CREATE TABLE produto (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    estoque INT
);

-- Dados iniciais (Opcional)
INSERT INTO produto (nome, preco, estoque) VALUES ('Notebook', 3500.00, 10);
INSERT INTO produto (nome, preco, estoque) VALUES ('Mouse Sem Fio', 89.90, 50);