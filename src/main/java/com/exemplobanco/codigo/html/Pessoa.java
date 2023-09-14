package com.exemplobanco.codigo.html;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*;

class Pessoa{
	public static Connection connect() throws Exception {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/pessoas";
        String username = "postgres";
        String password = "";
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        return connection;
    }
	
	public static ResultSet getDadosDoBanco() throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM pessoas";
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }
}