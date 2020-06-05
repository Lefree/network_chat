package ru.chat.server.core;

import java.sql.*;

public class Database {

    private static Connection connection;
    private static Statement statement;

    synchronized static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chat-server/chat_db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    synchronized static void disconnect() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    synchronized static String getNickname(String login, String password) {
        try {
            ResultSet rs = statement.executeQuery(String.format("" +
                    "SELECT " +
                    "   nickname " +
                    "FROM users " +
                    "WHERE login='%s' AND password='%s'", login, password));
            if (rs.next())
                return rs.getString("nickname");
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
        return null;
    }
}
