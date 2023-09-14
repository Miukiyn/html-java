package com.exemplobanco.codigo.html;

import static spark.Spark.*;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static spark.Spark.*;



public class Main {
    public static void main(String[] args) {
        // Configuração do banco de dados PostgreSQL
        String jdbcUrl = "jdbc:postgresql://localhost:5432/pessoas";
        String username = "postgres";
        String password = "";
        Pessoa dh = new Pessoa();
    
        

        // Configuração para servir arquivos estáticos (HTML)
        staticFiles.location("/public");
        
       //port(8080); //Porta teste a porta que esta sendo usada no programa eh 4567 (padrao)
        
        
        

        // Configuração da rota para inserção de dados
        post("/processar", (req, res) -> {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            
            // Recupere os dados do formulário HTML
            String nome = req.queryParams("nome");
            String idadeStr = req.queryParams("idade");
            int idade = Integer.parseInt(idadeStr); // Converte a idade para int
            String cidade = req.queryParams("cidade");
            
            
            // Insira os dados no banco de dados
            String sql = "INSERT INTO Pessoas (Nome, Idade, Cidade) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setInt(2, idade);
            stmt.setString(3, cidade);
            stmt.executeUpdate();
            
            // Feche a conexão com o banco de dados
            stmt.close();
            connection.close();
            
            // Redirecione para uma página de listagem
            res.redirect("/listagem.html");
            return null;
        });
        
     // Rota para exibir o HTML com os dados do banco de dados
        get("/listagem.html", (req, res) -> {
            try {
                ResultSet resultSet = dh.getDadosDoBanco();

                // Criar um objeto Map para armazenar os dados que serão exibidos no HTML
                Map<String, Object> model = new HashMap<>();
                model.put("dados", resultSet);

                // Renderizar o HTML com os dados
                return new ModelAndView(model, "public/listagem.html");
            } catch (Exception e) {
                e.printStackTrace();
                
                // Se ocorrer um erro, renderize uma página de erro ou uma mensagem de erro
                Map<String, Object> errorModel = new HashMap<>();
                errorModel.put("mensagem", "Erro ao recuperar dados do banco de dados.");

                return new ModelAndView(errorModel, "public/404.html");
            }
        }, new VelocityTemplateEngine());
    }
    
    
    public static List<Map<String, String>> obterDadosDoBanco() {
        List<Map<String, String>> dados = new ArrayList<>();

        String jdbcUrl = "jdbc:postgresql://localhost:5432/pessoas";
        String username = "postgres";
        String password = "";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String sql = "SELECT Nome, Idade, Cidade FROM Pessoas"; 

            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Map<String, String> registro = new HashMap<>();
                registro.put("nome", resultSet.getString("Nome"));
                registro.put("idade", resultSet.getString("Idade"));
                registro.put("cidade", resultSet.getString("Cidade"));
                dados.add(registro);
            }

            resultSet.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            // erro
        }

        return dados;
    }
    
}