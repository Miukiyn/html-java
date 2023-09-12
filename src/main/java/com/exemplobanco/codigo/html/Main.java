package com.exemplobanco.codigo.html;

import static spark.Spark.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Configuração do banco de dados PostgreSQL
        String jdbcUrl = "jdbc:postgresql://localhost:5432/pessoas";
        String username = "postgres";
        String password = "";

        // Configuração para servir arquivos estáticos (HTML, CSS, JavaScript, etc.)
        staticFiles.location("/public");
        
        // Define a porta desejada, por exemplo, 8080
        port(8080);

        // Rota para inserção de dados
        post("/processar", (req, res) -> {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            
            // Recupere os dados do formulário
            String nome = req.queryParams("nome");
            int idade = Integer.parseInt(req.queryParams("idade"));
            String cidade = req.queryParams("cidade");
            
            // Resto do código de inserção de dados aqui...
            
            // Após a inserção dos dados, você pode redirecionar para a página de listagem de pessoas
            res.redirect("/sucesso.html");
            return "Inserção bem-sucedida";
        });

        // Rota para listar as pessoas (novo endpoint)
        get("/listar", (req, res) -> {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            
            // Consulta SQL para recuperar os dados das pessoas
            String sql = "SELECT Nome, Idade, Cidade FROM Pessoas";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            
            // Lista para armazenar os dados das pessoas
            List<String> pessoas = new ArrayList<>();
            
            // Loop para recuperar os dados e adicioná-los à lista
            while (resultSet.next()) {
                String nome = resultSet.getString("Nome");
                int idade = resultSet.getInt("Idade");
                String cidade = resultSet.getString("Cidade");
                pessoas.add(nome + " | " + idade + " anos | " + cidade);
            }
            
            // Feche a conexão com o banco de dados
            resultSet.close();
            stmt.close();
            connection.close();
            
            // Renderize a página HTML com os dados das pessoas
            return renderListaDePessoas(pessoas);
        });
    }

    // Método para renderizar a lista de pessoas no formato HTML
    private static String renderListaDePessoas(List<String> pessoas) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>Listagem de Pessoas</h2>");
        html.append("<table>");
        html.append("<tr><th>Nome</th><th>Idade</th><th>Cidade</th></tr>");
        
        for (String pessoa : pessoas) {
            String[] dados = pessoa.split("\\|");
            String nome = dados[0].trim();
            String idade = dados[1].trim();
            String cidade = dados[2].trim();
            
            html.append("<tr><td>").append(nome).append("</td><td>").append(idade).append("</td><td>").append(cidade).append("</td></tr>");
        }
        
        html.append("</table></body></html>");
        return html.toString();
    }
}
