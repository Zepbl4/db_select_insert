package com.example.controller;

import com.example.models.User;

import java.sql.*;

public class DataBaseWorker {
    private final String url;
    private final String username;
    private final String password;

    public DataBaseWorker(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public User getUserByLogin(String login) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            connection = DriverManager.getConnection(url, username, password);

            statement = connection.createStatement();

            String sql = "SELECT users.login, users.password, users.date, emails.email " +
                    "FROM users LEFT JOIN emails ON users.login = emails.login " +
                    "WHERE users.login = '" + login + "'";

            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                
                user = new User(
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getDate("date"),
                        resultSet.getString("email")
                );

            }

        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            return null;

        } finally {

            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return user;
    }

    public int insertUser(User user) {
        String insertUserSql = "INSERT INTO users (login, password, date) VALUES (?, ?, ?)";
        String insertEmailSql = "INSERT INTO emails (login, email) VALUES (?, ?)";

        int affectedRows = 0;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {

            connection.setAutoCommit(false);

            try (PreparedStatement userStatement = connection.prepareStatement(insertUserSql);
                 PreparedStatement emailStatement = connection.prepareStatement(insertEmailSql)) {

                userStatement.setString(1, user.getLogin());
                userStatement.setString(2, user.getPassword());
                userStatement.setDate(3, new java.sql.Date(user.getDate().getTime()));
                affectedRows = userStatement.executeUpdate();

                emailStatement.setString(1, user.getLogin());
                emailStatement.setString(2, user.getEmail());
                affectedRows += emailStatement.executeUpdate();


                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error executing insert: " + e.getMessage());
                affectedRows = 0;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            affectedRows = 0;
        }

        return affectedRows;
    }
}

